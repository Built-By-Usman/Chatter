package com.muhammadosman.chatter.data.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.muhammadosman.chatter.R
import com.muhammadosman.chatter.data.models.User
import java.text.SimpleDateFormat
import java.util.*

class HomeAdapter(
    private val arrayList: ArrayList<User>,
    private val onClick: (User) -> Unit
) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.home_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = arrayList[position]
        holder.name.text = user.name
        holder.message.text = user.lastMessage

        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        holder.time.text = if (user.timestamp > 0) sdf.format(Date(user.timestamp)) else ""

        // Always set default avatar
        holder.avatar.setImageResource(R.drawable.avatar)

        holder.itemView.setOnClickListener { onClick(user) }
    }

    override fun getItemCount(): Int = arrayList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.avatar)
        val name: TextView = itemView.findViewById(R.id.person_name)
        val message: TextView = itemView.findViewById(R.id.person_message)
        val time: TextView = itemView.findViewById(R.id.message_time)
    }
}