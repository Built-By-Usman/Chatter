package com.muhammadosman.chatter.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammadosman.chatter.R
import com.muhammadosman.chatter.databinding.FragmentSignupBinding
import com.muhammadosman.chatter.utils

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.signupBtn.setOnClickListener { performSignup() }
        binding.login.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
    }

    private fun performSignup() {
        val name = binding.signupName.text.toString().trim()
        val email = binding.signupEmail.text.toString().trim()
        val password = binding.signupPassword.text.toString().trim()

        if (validateInput(name, email, password)) {
            signup(name, email, password)
        } else {
            utils.showToast(requireContext(), "Please enter all credentials")
        }
    }

    private fun signup(name: String, email: String, password: String) {
        // Show loading
        binding.loadingAnim.visibility = View.VISIBLE
        binding.signupBtn.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = auth.currentUser
                val userId = result.user?.uid ?: return@addOnSuccessListener

                user?.sendEmailVerification()
                    ?.addOnSuccessListener {
                        val userData = hashMapOf(
                            "id" to userId,
                            "name" to name,
                            "email" to email
                        )

                        db.collection("users")
                            .document(userId)
                            .set(userData)
                            .addOnSuccessListener {
                                // Hide loading
                                binding.loadingAnim.visibility = View.GONE
                                binding.signupBtn.isEnabled = true

                                utils.showToast(requireContext(), "Verify your email, then login")
                                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                            }
                            .addOnFailureListener {
                                binding.loadingAnim.visibility = View.GONE
                                binding.signupBtn.isEnabled = true
                                utils.showToast(requireContext(), "Failed to save user data")
                            }
                    }
                    ?.addOnFailureListener {
                        binding.loadingAnim.visibility = View.GONE
                        binding.signupBtn.isEnabled = true
                        utils.showToast(requireContext(), "Failed to send verification email")
                    }
            }
            .addOnFailureListener { e ->
                binding.loadingAnim.visibility = View.GONE
                binding.signupBtn.isEnabled = true
                utils.showToast(requireContext(), e.localizedMessage ?: "Signup failed")
            }
    }
    private fun validateInput(name: String, email: String, password: String): Boolean {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) return false
        if (password.length < 6) {
            utils.showToast(requireContext(), "Password must be at least 6 characters")
            return false
        }
        return true
    }
}