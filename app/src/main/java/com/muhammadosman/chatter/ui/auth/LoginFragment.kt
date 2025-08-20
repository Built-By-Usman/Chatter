package com.muhammadosman.chatter.ui.auth

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammadosman.chatter.R
import com.muhammadosman.chatter.databinding.FragmentLoginBinding
import com.muhammadosman.chatter.utils

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        preferences = requireContext().getSharedPreferences("chatter", Context.MODE_PRIVATE)

        binding.loginBtn.setOnClickListener { performLogin() }
        binding.signup.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    private fun performLogin() {
        val email = binding.loginEmail.text.toString()
        val password = binding.loginPassword.text.toString()

        if (validateInput(email, password)) {
            login(email, password)
        } else {
            utils.showToast(requireContext(), "Please Enter All Credentials")
        }
    }

    private fun login(email: String, password: String) {
        // Show loading
        binding.loadingAnim.visibility = View.VISIBLE
        binding.loginBtn.isEnabled = false

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = auth.currentUser
                val userId = result.user?.uid

                if (user != null && user.isEmailVerified && userId != null) {
                    db.collection("users")
                        .document(userId)
                        .get()
                        .addOnSuccessListener { doc ->
                            // Hide loading
                            binding.loadingAnim.visibility = View.GONE
                            binding.loginBtn.isEnabled = true

                            if (doc.exists()) {
                                val name = doc.getString("name") ?: ""
                                val emailDb = doc.getString("email") ?: ""

                                // ✅ Save data in SharedPreferences
                                preferences.edit().apply {
                                    putString("id", userId)
                                    putString("name", name)
                                    putString("email", emailDb)
                                    apply()
                                }

                                findNavController().navigate(R.id.action_loginFragment_to_home_fragment)
                            }
                        }
                        .addOnFailureListener {
                            binding.loadingAnim.visibility = View.GONE
                            binding.loginBtn.isEnabled = true
                            utils.showToast(requireContext(), "Failed to load user data")
                        }
                } else {
                    binding.loadingAnim.visibility = View.GONE
                    binding.loginBtn.isEnabled = true
                    utils.showToast(requireContext(), "Verify Your Email")
                }
            }
            .addOnFailureListener { e ->
                binding.loadingAnim.visibility = View.GONE
                binding.loginBtn.isEnabled = true
                utils.showToast(requireContext(), e.localizedMessage ?: "Login Failed")
            }
    }
    private fun validateInput(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
    }
}