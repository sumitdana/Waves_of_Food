package com.example.wavesoffood.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.wavesoffood.LoginActivity
import com.example.wavesoffood.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null
    private val auth = FirebaseAuth.getInstance()
    private val userRef = FirebaseDatabase.getInstance().getReference("user")

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            selectedImageUri = result.data?.data
            binding.profilepicture.setImageURI(selectedImageUri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load user data
        loadUserData()

        // Image click to pick
        binding.profilepicture.setOnClickListener {
            openGallery()
        }

        // Save changes
        binding.button4.setOnClickListener {
            saveUserProfile()
        }

        // Logout
        binding.logoutbutton.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun loadUserData() {
        val uid = auth.currentUser?.uid ?: return

        userRef.child(uid).get().addOnSuccessListener { snapshot ->
            binding.nameEditText.setText(snapshot.child("name").value?.toString() ?: "")
            binding.emailEditText.setText(snapshot.child("email").value?.toString() ?: "")
            binding.addressEditText.setText(snapshot.child("address").value?.toString() ?: "")
            binding.phoneEditText.setText(snapshot.child("phone").value?.toString() ?: "")
        }
    }

    private fun saveUserProfile() {
        val uid = auth.currentUser?.uid ?: return

        val updatedMap = mapOf(
            "name" to binding.nameEditText.text.toString().trim(),
            "address" to binding.addressEditText.text.toString().trim(),
            "phone" to binding.phoneEditText.text.toString().trim()
        )

        userRef.child(uid).updateChildren(updatedMap).addOnSuccessListener {
            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Update failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        pickImageLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}