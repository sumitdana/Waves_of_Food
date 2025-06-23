package com.example.wavesoffood

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wavesoffood.databinding.ActivityUpiPaymentBinding
import com.example.wavesoffood.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class UpiPaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpiPaymentBinding
    private var totalAmount: String = ""
    private var userId: String = ""
    private var phone: String = ""
    private var address: String = ""
    private lateinit var database: DatabaseReference
    private val cartList = mutableListOf<CartItems>()
    private val timeoutHandler = Handler(Looper.getMainLooper())
    private var paymentStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpiPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        totalAmount = intent.getStringExtra("totalAmount") ?: "0"
        userId = intent.getStringExtra("userId") ?: ""
        phone = intent.getStringExtra("phone") ?: ""
        address = intent.getStringExtra("address") ?: ""

        binding.upiInfoText.text = "You will pay ₹$totalAmount to UNI FOOD HUB"

        database = FirebaseDatabase.getInstance().reference
        loadCartItems()

        binding.payNowButton.setOnClickListener {
            paymentStarted = true
            makeUpiPayment("sumitdana21-1@okicici", totalAmount)

            // Set timeout for 60 seconds
            timeoutHandler.postDelayed({
                if (paymentStarted) {
                    Toast.makeText(this, "Payment timeout. Redirecting to cart.", Toast.LENGTH_SHORT).show()
                    notifyFailureAndRedirect()
                }
            }, 60000)
        }
    }

    private fun loadCartItems() {
        val cartRef = database.child("user").child(userId).child("CartItems")
        cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartList.clear()
                for (itemSnap in snapshot.children) {
                    val item = itemSnap.getValue(CartItems::class.java)
                    item?.let { cartList.add(it) }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun makeUpiPayment(upiId: String, amount: String) {
        val uri = Uri.parse("upi://pay").buildUpon()
            .appendQueryParameter("pa", upiId)
            .appendQueryParameter("pn", "Sumit Dana")
            .appendQueryParameter("tn", "Order Payment - Waves Of Food")
            .appendQueryParameter("am", amount)
            .appendQueryParameter("cu", "INR")
            .build()

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = uri
        }

        val chooser = Intent.createChooser(intent, "Pay with UPI")
        startActivityForResult(chooser, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        paymentStarted = false // stop timeout
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            val response = data?.getStringExtra("response") ?: ""
            if ((resultCode == Activity.RESULT_OK || resultCode == 11) &&
                response.contains("SUCCESS", ignoreCase = true)) {
                placeOrderAndClearCart()
            } else {
                notifyFailureAndRedirect()
            }
        }
    }

    private fun placeOrderAndClearCart() {
        val dateTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        val orderId = database.child("orders").push().key ?: return

        val orderMap = mapOf(
            "userId" to userId,
            "phone" to phone,
            "address" to address,
            "totalAmount" to totalAmount,
            "paymentMethod" to "UPI",
            "dateTime" to dateTime
        )

        val itemsMap = cartList.map {
            mapOf(
                "foodName" to it.foodName,
                "foodPrice" to it.foodPrice,
                "foodQuantity" to it.foodQuantity,
                "foodImage" to it.foodImage,
                "foodDescription" to it.foodDescription,
                "foodIngredient" to it.foodIngredient
            )
        }

        val orderRef = database.child("orders").child(orderId)
        val userOrderRef = database.child("user").child(userId).child("orders").child(orderId)

        orderRef.setValue(orderMap).addOnSuccessListener {
            orderRef.child("items").setValue(itemsMap)
            userOrderRef.setValue(orderMap).addOnSuccessListener {
                database.child("user").child(userId).child("CartItems").removeValue()
                Toast.makeText(this, "Payment Successful ✅", Toast.LENGTH_SHORT).show()
                val bottomSheet = CongratsBottomSheetFragment()
                bottomSheet.show(supportFragmentManager, "Congrats")
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show()
        }
    }

    private fun notifyFailureAndRedirect() {
        // Optionally notify with drawable
        val bottomSheet = NotificationBottomFragment()
        bottomSheet.show(supportFragmentManager, "Notify")

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("showCart", true)
        startActivity(intent)
        finish()
    }
}