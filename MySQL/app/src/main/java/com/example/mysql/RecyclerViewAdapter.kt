package com.example.mysql

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.example.mysql.data.Users
import com.example.mysql.fragments.BlankFragmentDirections

class RecyclerViewAdapter(private val usersList: List<Users>) :
    RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val loginTextView: TextView
        val idTextView: TextView
        val avatar_urlImageView: ImageView
        private val constraintLayout: ConstraintLayout

        init {
            loginTextView = view.findViewById(R.id.users_login)
            idTextView = view.findViewById(R.id.users_id)
            avatar_urlImageView = view.findViewById(R.id.users_avatar_url)
            constraintLayout = view.findViewById(R.id.users_container)
        }
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pictures_list_item_layout, parent, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = usersList[position]
        holder.loginTextView.text = user.login
        holder.idTextView.text = user.id.toString()
        holder.avatar_urlImageView.load(user.avatar_url) {
            memoryCachePolicy(CachePolicy.DISABLED)
            placeholder(R.drawable.ic_launcher_background)
            crossfade(1400)
        }

        holder.itemView.setOnClickListener {
            it.findNavController()
                .navigate(BlankFragmentDirections.actionBlankFragmentToBlankFragment2(user))
        }

    }

}