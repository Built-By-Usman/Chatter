package com.muhammadosman.chatter.ui.auth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.muhammadosman.chatter.R

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Load the splash screen layout
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Delay for 2 seconds (2000 ms) before navigating
        Handler(Looper.getMainLooper()).postDelayed({

            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser == null) {
                // If no user → go to Signup screen
                findNavController().navigate(R.id.action_splashFragment_to_signUpFragment)
            } else {
                // If logged in → go to Home screen
                findNavController().navigate(R.id.action_splashFragment_to_home_fragment)
            }

        }, 2000)  // 2 seconds delay (2000 ms)
    }
}