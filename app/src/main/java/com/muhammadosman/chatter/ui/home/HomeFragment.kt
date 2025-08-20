package com.muhammadosman.chatter.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammadosman.chatter.data.adapters.HomeAdapter
import com.muhammadosman.chatter.data.models.User
import com.muhammadosman.chatter.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var preferences: SharedPreferences
    private lateinit var userId: String
    private lateinit var arrayList: ArrayList<User>
    private lateinit var adapter: HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        userId = preferences.getString("id", "").toString()

        loadChat()

        binding.searchByEmail.setOnEditorActionListener { _, _, _ ->
            searchByEmail()
            true
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingAnim.visibility = View.VISIBLE
            binding.homeRecyclerView.visibility = View.GONE
        } else {
            binding.loadingAnim.visibility = View.GONE
            binding.homeRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun loadChat() {
        showLoading(true)

        db.collection("users")
            .document(userId)
            .collection("persons")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { docs ->
                arrayList.clear()
                for (doc in docs) {
                    val id = doc.id
                    val name = doc.getString("name") ?: ""
                    val email = doc.getString("email") ?: ""
                    val lastMessage = doc.getString("lastMessage") ?: ""
                    val timestamp = doc.getLong("timestamp") ?: 0L

                    val user = User(id, name, email, lastMessage, timestamp)
                    arrayList.add(user)
                }
                adapter.notifyDataSetChanged()
                showLoading(false)
            }
            .addOnFailureListener {
                showLoading(false)
            }
    }

    private fun searchByEmail() {
        val email = binding.searchByEmail.text.toString().trim()
        if (email.isEmpty()) return

        showLoading(true)

        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { docs ->
                arrayList.clear()
                for (doc in docs) {
                    val id = doc.id
                    val name = doc.getString("name") ?: ""
                    val emailFound = doc.getString("email") ?: ""

                    val user = User(id, name, emailFound)
                    arrayList.add(user)
                }
                adapter.notifyDataSetChanged()
                showLoading(false)
            }
            .addOnFailureListener {
                showLoading(false)
            }
    }

    private fun init() {
        arrayList = ArrayList()
        adapter = HomeAdapter(arrayList) { user ->
            val action = HomeFragmentDirections.actionHomeFragmentToChatFragment(user.id)
            findNavController().navigate(action)
        }

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        preferences = requireContext().getSharedPreferences("chatter", Context.MODE_PRIVATE)

        binding.homeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.homeRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}