package com.first.fintech.ui.services.view

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.first.fintech.R
import com.first.fintech.data.repository.services.ServiceRepository
import com.first.fintech.data.repository.subscriptions.SubscriptionRepository
import com.first.fintech.databinding.ActivityServicesBinding
import com.first.fintech.ui.services.view.adapter.ServicesAdapter
import com.first.fintech.ui.services.viewModel.ServicesViewModel
import com.first.fintech.ui.services.viewModel.ServicesViewModelFactory
import com.first.fintech.util.DrawerHelper
import com.first.fintech.util.NetworkUtils
import com.first.fintech.util.SessionManager
import androidx.activity.OnBackPressedCallback


class ServicesActivity : AppCompatActivity() {

    private lateinit var viewModel: ServicesViewModel
    private lateinit var binding: ActivityServicesBinding
    private lateinit var adapter: ServicesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DrawerHelper.setup(
            activity          = this,
            drawerLayout      = binding.drawerLayout,
            btnMenu           = binding.btnMenu,
            tvNavUserName     = binding.tvNavUserName,
            tvNavUserEmail    = binding.tvNavUserEmail,
            navServices       = binding.navServices,
            navSubscriptions  = binding.navSubscriptions,
            navLogout         = binding.navLogout
        )

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        setupRecyclerView()
        setupViewModel()
    }


    private fun setupRecyclerView() {
        adapter = ServicesAdapter { service ->
            if (!NetworkUtils.isConnected(this)) {
                showError("No internet connection")
                return@ServicesAdapter
            }

            val dialog = AlertDialog.Builder(this)
                .setTitle("Subscribe to ${service.serviceName}")
                .setMessage(
                    "You are about to subscribe to ${service.serviceName} for " +
                            "KES ${service.discountedPrice ?: service.pricing}"
                )
                .setPositiveButton("Subscribe") { _, _ ->
                    viewModel.subscribe(
                        subscriberEmail     = SessionManager.getEmail(this),
                        serviceName = service.serviceName,
                        amountPaid = service.discountedPrice ?: service.pricing

                    )
                }
                .setNegativeButton("Cancel", null)
                .create()
            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(this, R.color.orange))

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(this, R.color.orange))
            }

            dialog.show()

        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupViewModel() {
        val factory = ServicesViewModelFactory(
            ServiceRepository(this),
            SubscriptionRepository(this)
        )
        viewModel = ViewModelProvider(this, factory)[ServicesViewModel::class.java]

        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.isVisible = loading
        }

        viewModel.services.observe(this) { event ->
            event.getContentIfNotHandled()?.let { result ->
                result.onSuccess { response ->
                    if (response.data.isNullOrEmpty()) {
                        binding.tvEmptyState.isVisible = true
                    } else {
                        binding.tvEmptyState.isVisible = false
                        adapter.submitList(response.data)
                    }
                }
                result.onFailure { error ->
                    showError(error.message ?: "Failed to load services")
                }
            }
        }

        viewModel.subscribeResult.observe(this) { event ->
            event.getContentIfNotHandled()?.let { result ->
                result.onSuccess {
                    Snackbar.make(binding.root, "Subscribed successfully!", Snackbar.LENGTH_SHORT).show()
                }
                result.onFailure { error ->
                    showError(error.message ?: "Subscription failed")
                }
            }
        }

        if (!NetworkUtils.isConnected(this)) {
            showError("No internet connection")
        } else {
            viewModel.loadServices()
        }
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}