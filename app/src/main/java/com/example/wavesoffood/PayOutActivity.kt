package com.example.wavesoffood

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.adapter.PayOutCartAdapter
import com.example.wavesoffood.databinding.ActivityPayOutBinding
import com.example.wavesoffood.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class PayOutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPayOutBinding
    private lateinit var database: DatabaseReference
    private lateinit var cartList: MutableList<CartItems>
    private lateinit var adapter: PayOutCartAdapter

    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val userId = currentUser?.uid ?: ""

    private val hostelOptions = listOf(
        "Tagore Hostel", "Visvesvaraya Hostel", "Tilak Hostel", "Ramanujan Hostel",
        "Ambedkar Hostel", "Raman Hostel", "Subhash Hostel", "New Girls Hostel",
        "Saraswati Hostel", "Kalpana Chawla Hostel", "Kasturba Hostel"
    )

    private val paymentOptions = listOf("UPI", "Card", "NetBanking", "Cash On Delivery")
    private val enabledMethods = listOf("UPI", "Cash On Delivery")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference

        // Load user name
        currentUser?.let { user ->
            database.child("user").child(user.uid).child("name").get()
                .addOnSuccessListener { snapshot ->
                    binding.nameedittext.setText(snapshot.value?.toString() ?: "User")
                    binding.nameedittext.isEnabled = false
                }
        }

        // Set hostel dropdown
        val hostelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, hostelOptions)
        binding.addressAutoComplete.setAdapter(hostelAdapter)
        binding.addressAutoComplete.setOnClickListener {
            binding.addressAutoComplete.showDropDown()
        }

        // Payment method dropdown
        val paymentAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, paymentOptions)
        binding.paymentAutoComplete.setAdapter(paymentAdapter)
        binding.paymentAutoComplete.setOnClickListener {
            binding.paymentAutoComplete.showDropDown()
        }

        binding.paymentAutoComplete.setOnItemClickListener { _, _, position, _ ->
            val selected = paymentOptions[position]
            if (!enabledMethods.contains(selected)) {
                Toast.makeText(this, "$selected is currently not available", Toast.LENGTH_SHORT).show()
                binding.paymentAutoComplete.setText("")
            }
        }

        // RecyclerView setup
        cartList = mutableListOf()
        binding.myallitems.layoutManager = LinearLayoutManager(this)
        adapter = PayOutCartAdapter(this, cartList) {
            calculateTotal()
        }
        binding.myallitems.adapter = adapter
        loadCartItems()

        // Place order logic
        binding.placeOrderButton.setOnClickListener {
            val phone = binding.phoneedittext.text.toString().trim()
            val address = binding.addressAutoComplete.text.toString().trim()
            val paymentMethod = binding.paymentAutoComplete.text.toString().trim()

            if (phone.isEmpty() || address.isEmpty() || paymentMethod.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!enabledMethods.contains(paymentMethod)) {
                Toast.makeText(this, "Selected payment method is not available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save address and phone to DB
            currentUser?.let { user ->
                val userRef = database.child("user").child(user.uid)
                userRef.child("phone").setValue(phone)
                userRef.child("address").setValue(address)
                userRef.child("paymentMethod").setValue(paymentMethod)
            }

            val totalAmount = binding.totalAmountEditText.text.toString().replace("₹", "").trim()

            if (paymentMethod == "Cash On Delivery") {
                placeOrderDirectly(phone, address, paymentMethod, totalAmount)
            } else if (paymentMethod == "UPI") {
                val intent = Intent(this, UpiPaymentActivity::class.java)
                intent.putExtra("totalAmount", totalAmount)
                intent.putExtra("userId", userId)
                intent.putExtra("phone", phone)
                intent.putExtra("address", address)
                intent.putExtra("name", binding.nameedittext.text.toString())
                startActivity(intent)
            }
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
                adapter.notifyDataSetChanged()
                calculateTotal()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PayOutActivity, "Error loading cart: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun calculateTotal() {
        var total = 0
        for (item in cartList) {
            val price = item.foodPrice?.toIntOrNull() ?: 0
            val qty = item.foodQuantity ?: 1
            total += price * qty
        }
        binding.totalAmountEditText.setText("₹$total")
    }

    private fun placeOrderDirectly(phone: String, address: String, paymentMethod: String, totalAmount: String) {
        val dateTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        val orderId = database.child("orders").push().key ?: return

        val orderMap = mapOf(
            "userId" to userId,
            "name" to binding.nameedittext.text.toString(),
            "phone" to phone,
            "address" to address,
            "paymentMethod" to paymentMethod,
            "totalAmount" to totalAmount,
            "dateTime" to dateTime,
            "status" to "pending"
        )

        val orderItemsMap = cartList.map {
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

        orderRef.setValue(orderMap).addOnSuccessListener {
            orderRef.child("items").setValue(orderItemsMap).addOnSuccessListener {
                val userOrderRef = database.child("user").child(userId).child("orders").child(orderId)
                userOrderRef.setValue(orderMap).addOnSuccessListener {
                    userOrderRef.child("items").setValue(orderItemsMap)
                }

                database.child("user").child(userId).child("CartItems").removeValue()
                Toast.makeText(this, "Order placed successfully (COD) ✅", Toast.LENGTH_SHORT).show()
                val bottomSheet = CongratsBottomSheetFragment()
                bottomSheet.show(supportFragmentManager, "Congrats")
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to save order items", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show()
        }
    }

    fun placeOrderAfterUpiSuccess(phone: String, address: String, paymentMethod: String) {
        val totalAmount = binding.totalAmountEditText.text.toString().replace("₹", "").trim()
        placeOrderDirectly(phone, address, paymentMethod, totalAmount)
    }
}