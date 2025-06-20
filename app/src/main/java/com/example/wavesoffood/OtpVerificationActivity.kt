package com.example.wavesoffood

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wavesoffood.databinding.ActivityOtpVerificationBinding

class OtpVerificationActivity : AppCompatActivity() {
    private val binding: ActivityOtpVerificationBinding by lazy {
        ActivityOtpVerificationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Confirm button click
        binding.confirmbutton.setOnClickListener {
            val otp = binding.otp1.text.toString() +
                    binding.otp2.text.toString() +
                    binding.otp3.text.toString() +
                    binding.otp4.text.toString() +
                    binding.otp5.text.toString() +
                    binding.otp6.text.toString()

            val correctOtp = "123456" // Replace with actual OTP

            if (otp == correctOtp) {
                val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                sharedPreferences.edit().apply {
                    putBoolean("isLoggedIn", true)
                    putString("email", intent.getStringExtra("email"))
                    apply()
                }

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }

        // Back button
        binding.backbutton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        setupOtpInputs()
    }

    private fun setupOtpInputs() {
        val otp1 = binding.otp1
        val otp2 = binding.otp2
        val otp3 = binding.otp3
        val otp4 = binding.otp4
        val otp5 = binding.otp5
        val otp6 = binding.otp6

        otp1.addTextChangedListener(GenericTextWatcher(otp1, otp2, null))
        otp2.addTextChangedListener(GenericTextWatcher(otp2, otp3, otp1))
        otp3.addTextChangedListener(GenericTextWatcher(otp3, otp4, otp2))
        otp4.addTextChangedListener(GenericTextWatcher(otp4, otp5, otp3))
        otp5.addTextChangedListener(GenericTextWatcher(otp5, otp6, otp4))
        otp6.addTextChangedListener(GenericTextWatcher(otp6, null, otp5))

        otp2.setOnKeyListener(GenericKeyEvent(otp2, otp1))
        otp3.setOnKeyListener(GenericKeyEvent(otp3, otp2))
        otp4.setOnKeyListener(GenericKeyEvent(otp4, otp3))
        otp5.setOnKeyListener(GenericKeyEvent(otp5, otp4))
        otp6.setOnKeyListener(GenericKeyEvent(otp6, otp5))
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