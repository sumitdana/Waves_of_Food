package com.example.wavesoffood

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wavesoffood.databinding.ActivityOtpVerificationBinding
import com.example.wavesoffood.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class OtpVerificationActivity : AppCompatActivity() {

    private val binding: ActivityOtpVerificationBinding by lazy {
        ActivityOtpVerificationBinding.inflate(layoutInflater)
    }

    private lateinit var receivedOtp: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var userName: String
    private var isSignUp: Boolean = false

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val database by lazy { FirebaseDatabase.getInstance().reference }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Get data from intent
        receivedOtp = intent.getStringExtra("otp") ?: ""
        email = intent.getStringExtra("email") ?: ""
        password = intent.getStringExtra("password") ?: ""
        userName = intent.getStringExtra("name") ?: ""
        isSignUp = intent.getBooleanExtra("isSignUp", false)

        setupOtpInputs()

        binding.confirmbutton.setOnClickListener {
            val enteredOtp = binding.otp1.text.toString() +
                    binding.otp2.text.toString() +
                    binding.otp3.text.toString() +
                    binding.otp4.text.toString() +
                    binding.otp5.text.toString() +
                    binding.otp6.text.toString()

            if (enteredOtp == receivedOtp) {
                if (isSignUp) {
                    signUpWithFirebase()
                } else {
                    // Email/password login flow
                    loginWithFirebase()
                }
            } else {
                Toast.makeText(this, "Invalid OTP ❌", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backbutton.setOnClickListener {
            finish()
        }
    }

    private fun signUpWithFirebase() {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val userModel = UserModel(userName, email, password)
                    database.child("user").child(uid).setValue(userModel)
                    Toast.makeText(this, "Account created successfully 🎉", Toast.LENGTH_SHORT).show()
                    goToMain()
                } else {
                    Toast.makeText(this, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loginWithFirebase() {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful ✅", Toast.LENGTH_SHORT).show()
                    goToMain()
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun goToMain() {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putBoolean("isLoggedIn", true)
            putString("email", email)
            apply()
        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun setupOtpInputs() {
        val otps = listOf(
            binding.otp1, binding.otp2, binding.otp3,
            binding.otp4, binding.otp5, binding.otp6
        )

        for (i in otps.indices) {
            otps[i].addTextChangedListener(GenericTextWatcher(otps[i], otps.getOrNull(i + 1), otps.getOrNull(i - 1)))
            if (i > 0) {
                otps[i].setOnKeyListener(GenericKeyEvent(otps[i], otps[i - 1]))
            }
        }
    }

    inner class GenericTextWatcher(
        private val currentView: EditText,
        private val nextView: EditText?,
        private val previousView: EditText?
    ) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (s?.length == 1) {
                nextView?.requestFocus()
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    inner class GenericKeyEvent(
        private val currentView: EditText,
        private val previousView: EditText?
    ) : View.OnKeyListener {
        override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                if (currentView.text.isEmpty()) {
                    previousView?.requestFocus()
                    return true
                }
            }
            return false
        }
    }
}