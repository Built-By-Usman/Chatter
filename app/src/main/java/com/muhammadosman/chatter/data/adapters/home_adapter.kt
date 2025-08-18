package com.muhammadosman.chatter.data.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.muhammadosman.chatter.R
import com.muhammadosman.chatter.data.models.User

class home_adapter(private val arrayList: ArrayList<User>) :
    RecyclerView.Adapter<home_adapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.home_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val user=arrayList[position]
        holder.name.text=user.name
        holder.message.text=user.name
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name=itemView.findViewById<TextView>(R.id.person_name)
        val message=itemView.findViewById<TextView>(R.id.person_message)
    }
}