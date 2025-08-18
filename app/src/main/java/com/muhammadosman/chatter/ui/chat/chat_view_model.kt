package com.muhammadosman.chatter.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammadosman.chatter.data.repositories.ChatRepository
import kotlinx.coroutines.launch

class chat_view_model(private val repo: ChatRepository) : ViewModel() {

    fun sendMessage(receiverId: String, text: String) {
        viewModelScope.launch {
            repo.sendMessage(receiverId, text)
        }
    }
}