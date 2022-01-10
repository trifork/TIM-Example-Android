package com.trifork.timandroid.welcome

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trifork.timandroid.databinding.ListItemUserLoginBinding
import com.trifork.timandroid.util.AuthenticatedUsers

class UserLoginAdapter(private val userLoginAdapterClickListener: UserLoginAdapterClickListener, private val authenticatedUsers: AuthenticatedUsers) : RecyclerView.Adapter<UserLoginAdapter.UserViewHolder>() {

    interface UserLoginAdapterClickListener {
        fun userLoginClick(item: String)
    }

    var userLogins: List<String> = listOf<String>().toList()

    class UserViewHolder(private val listItemNoteBinding: ListItemUserLoginBinding, val authenticatedUsers: AuthenticatedUsers) : RecyclerView.ViewHolder(listItemNoteBinding.root) {
        private val textViewName = listItemNoteBinding.textViewName

        fun bind(currentUserLogin: String) {
            textViewName.text = authenticatedUsers.getName(currentUserLogin) ?: currentUserLogin
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = ListItemUserLoginBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(view, authenticatedUsers)
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