package com.muhammadosman.chatter.data.models

data class User(
    val id: String = "",
    var name: String = "",
    var email: String = "",
    val lastMessage: String = "",
    val timestamp: Long = 0L
)