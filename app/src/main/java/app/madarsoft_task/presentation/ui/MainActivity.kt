package app.madarsoft_task.presentation.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import app.madarsoft_task.R
import app.madarsoft_task.data.database.User
import app.madarsoft_task.databinding.ActivityMainBinding
import app.madarsoft_task.presentation.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupClickEvents()
    }

    private fun setupClickEvents() {
        binding.apply {
            btnSave.setOnClickListener { handleSaveUser() }
            btnShowUsers.setOnClickListener { navigateToUsersActivity() }
            etGender.setOnClickListener { showGenderDialog() }
            etGender.isFocusable = false
            etGender.isFocusableInTouchMode = false
        }
    }

    private fun handleSaveUser() {
        binding.root.hideKeyboard()
        val user = gatherUserInput() ?: return
        saveUser(user)
    }

    private fun gatherUserInput(): User? {
        val name = binding.etName.text.toString()
        val age = binding.etAge.text.toString().toIntOrNull()
        val jobTitle = binding.etJobTitle.text.toString()
        val gender = binding.etGender.text.toString()

        if (name.isEmpty() || age == null || jobTitle.isEmpty() || gender.isEmpty()) {
            showSnackbar(getString(R.string.fill_data))
            return null
        }

        return User(name = name, age = age, jobTitle = jobTitle, gender = gender)
    }

    private fun saveUser(user: User) {
        lifecycleScope.launch {
            userViewModel.insertUser(user)
            clearInputFields()
            showSuccessMessageAndNavigate()
        }
    }

    private fun showSuccessMessageAndNavigate() {
        Snackbar.make(binding.root, getString(R.string.user_saved), Snackbar.LENGTH_SHORT)
            .setAction(getString(R.string.view_users)) { navigateToUsersActivity() }
            .show()
    }

    private fun navigateToUsersActivity() {
        startActivity(Intent(this, UsersActivity::class.java))
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun clearInputFields() {
        binding.etName.text.clear()
        binding.etAge.text.clear()
        binding.etJobTitle.text.clear()
        binding.etGender.text.clear()
    }

    private fun showGenderDialog() {
        val genderOptions = arrayOf(getString(R.string.male), getString(R.string.female))
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.select_gender))
            .setSingleChoiceItems(genderOptions, -1) { dialog, which ->
                binding.etGender.setText(genderOptions[which])
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}