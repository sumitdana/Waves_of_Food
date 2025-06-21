package com.example.wavesoffood

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wavesoffood.databinding.ActivityPayOutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class PayOutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPayOutBinding
    private val database = FirebaseDatabase.getInstance().reference
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private val hostelOptions = listOf(
        "Tagore Hostel",
        "Visvesvaraya Hostel",
        "Tilak Hostel",
        "Ramanujan Hostel",
        "Ambedkar Hostel",
        "Raman Hostel",
        "Subhash Hostel",
        "New Girls Hostel",
        "Saraswati Hostel",
        "Kalpana Chawla Hostel",
        "Kasturba Hostel"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Set user's name (non-editable)
        currentUser?.let { user ->
            database.child("user").child(user.uid).child("name")
                .get()
                .addOnSuccessListener { snapshot ->
                    val name = snapshot.value?.toString() ?: "User"
                    binding.nameedittext.setText(name)
                    binding.nameedittext.isEnabled = false
                }
        }

        // ✅ Get and show total amount from intent
        val totalAmount = intent.getIntExtra("totalAmount", 0)
        binding.totalAmountEditText.setText("₹$totalAmount")

        // ✅ Set up hostel dropdown
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, hostelOptions)
        binding.addressAutoComplete.setAdapter(adapter)
        binding.addressAutoComplete.setOnClickListener {
            binding.addressAutoComplete.showDropDown()
        }

        // ✅ Handle "Place Order" button
        binding.placeOrderButton.setOnClickListener {
            val phone = binding.phoneedittext.text.toString().trim()
            val address = binding.addressAutoComplete.text.toString().trim()

            if (phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Please enter phone and select address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            currentUser?.let { user ->
                val userRef = database.child("user").child(user.uid)
                userRef.child("phone").setValue(phone)
                userRef.child("address").setValue(address)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Order placed successfully ✅", Toast.LENGTH_SHORT).show()
                        val bottomSheet = CongratsBottomSheetFragment()
                        bottomSheet.show(supportFragmentManager, "Congrats")
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to save order details", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}