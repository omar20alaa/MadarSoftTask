package app.madarsoft_task.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import app.madarsoft_task.R
import app.madarsoft_task.data.database.User
import app.madarsoft_task.databinding.ActivityUsersBinding
import app.madarsoft_task.presentation.adapter.UserAdapter
import app.madarsoft_task.presentation.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class UsersActivity : AppCompatActivity(), UserAdapter.OnUserClickListener {

    private lateinit var binding: ActivityUsersBinding
    private lateinit var userAdapter: UserAdapter
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeUserList()
        setupClearUsersClickListener()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(this)
        binding.recyclerView.adapter = userAdapter
    }

    private fun observeUserList() {
        lifecycleScope.launch {
            userViewModel.users.collectLatest { users ->
                userAdapter.submitList(users)
                updateUI(users)
            }
        }
        userViewModel.getAllUsers()
    }

    private fun updateUI(users: List<User>) {
        binding.tvNoUsers.visibility = if (users.isEmpty()) View.VISIBLE else View.GONE
        binding.ivClearUsers.visibility = if (users.isNotEmpty()) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (users.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun setupClearUsersClickListener() {
        binding.ivClearUsers.setOnClickListener { showClearUsersConfirmationDialog() }
    }

    private fun showClearUsersConfirmationDialog() {
        createAlertDialog(
            title = getString(R.string.confirm_clear_users),
            message = getString(R.string.clear_users_message),
            positiveAction = { clearUsersFromDatabase() }
        ).show()
    }

    private fun clearUsersFromDatabase() {
        lifecycleScope.launch {
            userViewModel.clearAllUsers()
            showSnackbar(getString(R.string.users_cleared))
        }
    }

    override fun onClearUserClicked(user: User) {
        showClearConfirmationDialog(user)
    }

    private fun showClearConfirmationDialog(user: User) {
        createAlertDialog(
            title = getString(R.string.confirm_clear_user),
            message = getString(R.string.clear_user_message, user.name),
            positiveAction = { clearSpecificUserFromDatabase(user) }
        ).show()
    }

    private fun clearSpecificUserFromDatabase(user: User) {
        lifecycleScope.launch {
            userViewModel.deleteUser(user)
            showSnackbar(getString(R.string.user_cleared, user.name))
        }
    }

    private fun createAlertDialog(title: String, message: String, positiveAction: () -> Unit): AlertDialog {
        return AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                positiveAction()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
            .create()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}