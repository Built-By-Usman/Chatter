package com.muhammadosman.chatter.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.muhammadosman.chatter.R

class login_fragment : Fragment() {

    private lateinit var viewModel: auth_view_model

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login_fragment, container, false)
    }
}