package com.muhammadosman.chatter.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import com.muhammadosman.chatter.data.repositories.ChatRepository

class auth_view_model(private val repo: ChatRepository) : ViewModel() {

    val loginResult = MutableLiveData<Boolean>()
    val registerResult = MutableLiveData<Boolean>()

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            val success = repo.registerUser(name, email, password)
            registerResult.postValue(success)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val success = repo.loginUser(email, password)
            loginResult.postValue(success)
        }
    }
}