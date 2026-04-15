package com.first.fintech.ui.auth.login.view

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.first.fintech.data.repository.auth.AuthRepository
import com.first.fintech.databinding.ActivityLoginBinding
import com.first.fintech.ui.auth.login.viewModel.LoginViewModel
import com.first.fintech.ui.auth.login.viewModel.LoginViewModelFactory
import com.first.fintech.ui.auth.register.view.RegisterActivity
import com.first.fintech.ui.services.view.ServicesActivity

import com.first.fintech.util.NetworkUtils
import com.first.fintech.util.SessionManager
import android.text.InputType
import androidx.core.view.WindowInsetsControllerCompat
import com.first.fintech.R


class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (SessionManager.isLoggedIn(this)) {
            goToServices()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = getColor(R.color.white)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true


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

        val factory = LoginViewModelFactory(AuthRepository(this))
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        binding.btnLogin.setOnClickListener {
            if (!NetworkUtils.isConnected(this)) {
                showError("No internet connection")
                return@setOnClickListener
            }
            viewModel.login(
                email    = binding.etEmail.text.toString().trim(),
                password = binding.etPassword.text.toString().trim()
            )
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        viewModel.validationError.observe(this) { error ->
            error?.let { showError(it) }
        }

        viewModel.isLoading.observe(this) { loading ->
            binding.btnLogin.isEnabled    = !loading
            binding.progressBar.isVisible = loading
        }

        viewModel.loginResult.observe(this) { event ->
            event.getContentIfNotHandled()?.let { result ->
                result.onSuccess { response ->
                    SessionManager.saveSession(
                        context = this,
                        token   = response.data?.token ?: "",
                        email   = response.data?.email ?: "",
                        name    = response.data?.fullName ?: ""
                    )
                    goToServices()
                }
                result.onFailure { error ->
                    showError(error.message ?: "Login failed")
                }
            }
        }
    }

    private fun goToServices() {
        startActivity(Intent(this, ServicesActivity::class.java))
        finish()
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}