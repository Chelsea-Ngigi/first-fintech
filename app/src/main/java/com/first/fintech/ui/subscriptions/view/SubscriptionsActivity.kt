package com.first.fintech.ui.subscriptions.view

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.first.fintech.data.repository.auth.AuthRepository
import com.first.fintech.data.repository.subscriptions.SubscriptionRepository
import com.first.fintech.databinding.ActivityLoginBinding
import com.first.fintech.databinding.ActivitySubscriptionsBinding
import com.first.fintech.ui.auth.login.viewModel.LoginViewModel
import com.first.fintech.ui.auth.login.viewModel.LoginViewModelFactory
import com.first.fintech.ui.auth.register.view.RegisterActivity
import com.first.fintech.ui.subscriptions.view.adapter.SubscriptionsAdapter
import com.first.fintech.ui.subscriptions.viewModel.SubscriptionsViewModel
import com.first.fintech.ui.subscriptions.viewModel.SubscriptionsViewModelFactory

import com.first.fintech.util.NetworkUtils
import com.first.fintech.util.SessionManager

class SubscriptionsActivity : AppCompatActivity() {

    private lateinit var viewModel: SubscriptionsViewModel
    private lateinit var binding: ActivitySubscriptionsBinding
    private lateinit var adapter: SubscriptionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubscriptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupViewModel()

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        adapter = SubscriptionsAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupViewModel() {
        val factory = SubscriptionsViewModelFactory(SubscriptionRepository(this))
        viewModel = ViewModelProvider(this, factory)[SubscriptionsViewModel::class.java]

        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.isVisible = loading
        }

        viewModel.subscriptions.observe(this) { event ->
            event.getContentIfNotHandled()?.let { result ->
                result.onSuccess { response ->
                    if (response.data.isNullOrEmpty()) {
                        binding.tvEmptyState.isVisible = true
                        binding.tvEmptyState.text      = "You have no active subscriptions"
                    } else {
                        binding.tvEmptyState.isVisible = false
                        adapter.submitList(response.data)
                    }
                }
                result.onFailure { error ->
                    Snackbar.make(binding.root, error.message ?: "Failed to load", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        if (!NetworkUtils.isConnected(this)) {
            Snackbar.make(binding.root, "No internet connection", Snackbar.LENGTH_LONG).show()
        } else {
            viewModel.loadSubscriptions(SessionManager.getEmail(this))
        }
    }
}