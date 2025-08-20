package com.muhammadosman.chatter.data.models

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val lastMessage: String = "",
    val timestamp: Long = 0L
)