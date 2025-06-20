package com.example.wavesoffood

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wavesoffood.network.RetrofitInstance
import com.example.wavesoffood.databinding.ActivitySigninBinding
import com.example.wavesoffood.model.OtpRequest
import com.example.wavesoffood.model.OtpResponse
import com.example.wavesoffood.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SigninActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var userName: String

    private val RC_SIGN_IN = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.signinbutton.setOnClickListener {
            userName = binding.userName.text.toString()
            email = binding.email.text.toString().trim()
            password = binding.password.text.toString().trim()

            if (email.isBlank() || password.isBlank() || userName.isBlank()) {
                Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()
            } else {
                sendOtpToEmail(email)
            }
        }

        binding.alreadyhaveanacc.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.googlebutton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    private fun sendOtpToEmail(email: String) {
       val request = OtpRequest(email)

        //val request = mapOf("email" to email)

        RetrofitInstance.api.sendOtp(request).enqueue(object : Callback<OtpResponse> {
            override fun onResponse(call: Call<OtpResponse>, response: Response<OtpResponse>) {
                if (response.isSuccessful && response.body()?.success == true && response.body()?.otp != null) {
                    val otp = response.body()?.otp
                    val intent = Intent(this@SigninActivity, OtpVerificationActivity::class.java)
                    intent.putExtra("otp", otp)
                    intent.putExtra("email", email)
                    intent.putExtra("password", password)
                    intent.putExtra("name", userName)
                    intent.putExtra("isSignUp", true)
                    startActivity(intent)
                } else {
                    Log.e("OTP_SEND", "Failed response: ${response.errorBody()?.string()}")
                    Toast.makeText(this@SigninActivity, "Failed to send OTP: ${response.body()?.message ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OtpResponse>, t: Throwable) {
                Toast.makeText(this@SigninActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("OTP_SEND", "Send OTP failed", t)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign-in failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                saveUserDataFromGoogle(user)
                Toast.makeText(this, "Signed in with Google", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Firebase auth failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserDataFromGoogle(user: FirebaseUser?) {
        user?.let {
            val userModel = UserModel(
                user.displayName ?: "No Name",
                user.email ?: "No Email",
                "GoogleSignIn"
            )
            database.child("user").child(user.uid).setValue(userModel)
        }
    }
}