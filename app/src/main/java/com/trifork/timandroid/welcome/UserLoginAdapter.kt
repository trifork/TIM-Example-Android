package com.trifork.timandroid.welcome

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trifork.timandroid.databinding.ListItemUserLoginBinding

class UserLoginAdapter(private val userLoginAdapterClickListener: UserLoginAdapterClickListener) : RecyclerView.Adapter<UserLoginAdapter.UserViewHolder>() {

    interface UserLoginAdapterClickListener {
        fun userLoginClick(item: String)
    }

    var userLogins: MutableList<String> = listOf<String>().toMutableList()

    class UserViewHolder(listItemNoteBinding: ListItemUserLoginBinding) : RecyclerView.ViewHolder(listItemNoteBinding.root) {
        private val textViewName = listItemNoteBinding.textViewName

        fun bind(currentUserLogin: String) {
            textViewName.text = currentUserLogin
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = ListItemUserLoginBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(userLogins[position])
        holder.itemView.setOnClickListener {
            userLoginAdapterClickListener.userLoginClick(userLogins[position])
        }
    }

    override fun getItemCount(): Int {
        return userLogins.size
    }
}