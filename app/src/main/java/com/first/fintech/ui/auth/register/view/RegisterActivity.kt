package com.first.fintech.ui.auth.register.view

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.first.fintech.R
import com.first.fintech.data.repository.auth.AuthRepository
import com.first.fintech.databinding.ActivityRegisterBinding
import com.first.fintech.ui.auth.login.view.LoginActivity
import com.first.fintech.ui.auth.register.viewModel.RegisterViewModel
import com.first.fintech.ui.auth.register.viewModel.RegisterViewModelFactory
import com.first.fintech.util.NetworkUtils

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewModel: RegisterViewModel
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var isPasswordVisible = false

        binding.passwordLayout.setEndIconOnClickListener {

            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                binding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

                binding.passwordLayout.endIconDrawable =
                    ContextCompat.getDrawable(this, R.drawable.ic_eye_open)
            } else {
                binding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

                binding.passwordLayout.endIconDrawable =
                    ContextCompat.getDrawable(this, R.drawable.ic_eye_close)
            }

            binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
        }

        val factory = RegisterViewModelFactory(AuthRepository(this))
        viewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]

        binding.btnRegister.setOnClickListener {
            if (!NetworkUtils.isConnected(this)) {
                showError("No internet connection")
                return@setOnClickListener
            }

            val firstName = binding.etFirstName.text?.toString()?.trim().orEmpty()
            val lastName = binding.etLastName.text?.toString()?.trim().orEmpty()

            if (firstName.isEmpty()) {
                showError("First name is required")
                return@setOnClickListener
            }

            if (lastName.isEmpty()) {
                showError("Last name is required")
                return@setOnClickListener
            }

            val fullName = "$firstName $lastName".trim()

            viewModel.register(
                fullName     = fullName,
                email    = binding.etEmail.text.toString().trim(),
                msisdn    = binding.etPhone.text.toString().trim(),
                credentials = binding.etPassword.text.toString().trim()
            )
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        viewModel.validationError.observe(this) { error ->
            error?.let { showError(it) }
        }

        viewModel.isLoading.observe(this) { loading ->
            binding.btnRegister.isEnabled = !loading
            binding.progressBar.isVisible = loading
        }

        viewModel.registerResult.observe(this) { event ->
            event.getContentIfNotHandled()?.let { result ->
                result.onSuccess {
                    Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                result.onFailure { error ->
                    showError(error.message ?: "Registration failed")
                }
            }
        }
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}