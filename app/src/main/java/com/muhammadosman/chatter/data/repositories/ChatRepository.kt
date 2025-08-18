package com.muhammadosman.chatter.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammadosman.chatter.data.models.Message
import com.muhammadosman.chatter.data.models.User
import kotlinx.coroutines.tasks.await

class ChatRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private fun getCurrentUserId() = auth.currentUser?.uid

    suspend fun registerUser(name: String, email: String, password: String): Boolean {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.uid?.let { uid ->
                val user = User(uid, name, email)
                db.collection("users").document(uid).set(user).await()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun sendMessage(receiverId: String, text: String) {
        val message = Message(
            messageId = db.collection("messages").document().id,
            senderId = getCurrentUserId() ?: "",
            receiverId = receiverId,
            text = text
        )
        db.collection("messages").document(message.messageId).set(message).await()
    }
}