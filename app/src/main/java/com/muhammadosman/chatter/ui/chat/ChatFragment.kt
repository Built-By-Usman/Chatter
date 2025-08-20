package com.muhammadosman.chatter.ui.chat

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.muhammadosman.chatter.data.adapters.ChatAdapter
import com.muhammadosman.chatter.data.models.Message
import com.muhammadosman.chatter.databinding.FragmentChatBinding

class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var preferences: SharedPreferences
    private lateinit var userId: String
    private lateinit var personId: String
    private lateinit var adapter: ChatAdapter
    private lateinit var messages: ArrayList<Message>

    private val args: ChatFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        personId = args.personId

        loadPersonInfo()
        loadMessages()

        binding.sendBtn.setOnClickListener {
            val text = binding.messageEt.text.toString().trim()
            if (text.isNotEmpty()) {
                sendMessage(text)
                binding.messageEt.setText("")
            }
        }
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        preferences = requireContext().getSharedPreferences("chatter", Context.MODE_PRIVATE)
        userId = preferences.getString("id", "").orEmpty()

        messages = ArrayList()
        adapter = ChatAdapter(messages, userId)

        binding.chatRecycler.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        binding.chatRecycler.adapter = adapter
    }
    private fun loadPersonInfo() {
        db.collection("users").document(personId).get()
            .addOnSuccessListener { doc ->
                binding.personName.text = doc.getString("name") ?: "Unknown"
            }
    }

    private fun loadMessages() {
        db.collection("users")
            .document(userId)
            .collection("persons")
            .document(personId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                if (_binding == null) return@addSnapshotListener

                if (snapshot != null) {
                    messages.clear()
                    for (doc in snapshot.documents) {
                        val message = doc.toObject(Message::class.java)
                        if (message != null) messages.add(message)
                    }
                    adapter.notifyDataSetChanged()

                    if (messages.isEmpty()) {
                        binding.emptyAnimation.visibility = View.VISIBLE
                        binding.chatRecycler.visibility = View.GONE
                    } else {
                        binding.emptyAnimation.visibility = View.GONE
                        binding.chatRecycler.visibility = View.VISIBLE
                        binding.chatRecycler.scrollToPosition(messages.size - 1)
                    }
                }
            }
    }

    private fun sendMessage(text: String) {
        val msgId = db.collection("messages").document().id
        val timestamp = System.currentTimeMillis()

        val msg = Message(
            messageId = msgId,
            senderId = userId,
            receiverId = personId,
            text = text,
            timestamp = timestamp
        )

        // ✅ Save message for current user
        db.collection("users")
            .document(userId)
            .collection("persons")
            .document(personId)
            .collection("messages")
            .document(msgId)
            .set(msg)

        // ✅ Fetch receiver info and update both sides
        db.collection("users").document(personId).get().addOnSuccessListener { receiverDoc ->
            val receiverName = receiverDoc.getString("name") ?: "Unknown"
            val receiverEmail = receiverDoc.getString("email") ?: ""

            val senderName = auth.currentUser?.displayName ?: "Me"
            val senderEmail = auth.currentUser?.email ?: ""

            val lastMessageUpdateForSender = mapOf(
                "name" to receiverName,
                "email" to receiverEmail,
                "lastMessage" to text,
                "timestamp" to timestamp
            )

            val lastMessageUpdateForReceiver = mapOf(
                "name" to senderName,
                "email" to senderEmail,
                "lastMessage" to text,
                "timestamp" to timestamp
            )

            // Update for current user
            db.collection("users").document(userId)
                .collection("persons").document(personId)
                .set(lastMessageUpdateForSender, com.google.firebase.firestore.SetOptions.merge())

            // Update for receiver
            db.collection("users").document(personId)
                .collection("persons").document(userId)
                .set(lastMessageUpdateForReceiver, com.google.firebase.firestore.SetOptions.merge())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}