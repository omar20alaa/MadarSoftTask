package app.madarsoft_task.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.madarsoft_task.data.database.User
import app.madarsoft_task.databinding.ItemUserBinding

class UserAdapter(private val listener: OnUserClickListener) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var users: List<User> = emptyList()

    fun submitList(userList: List<User>) {
        users = userList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)

        holder.binding.ivClearUser.setOnClickListener {
            listener.onClearUserClicked(user)
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.tvUserName.text = user.name
            binding.tvUserAge.text = user.age.toString()
            binding.tvUserJobTitle.text = user.jobTitle
            binding.tvUserGender.text = user.gender
        }
    }

    interface OnUserClickListener {
        fun onClearUserClicked(user: User)
    }
}