package com.first.fintech.ui.services.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.first.fintech.data.repository.services.ServiceRepository
import com.first.fintech.data.repository.subscriptions.SubscriptionRepository


class ServicesViewModelFactory(
    private val serviceRepo: ServiceRepository,
    private val subscriptionRepo: SubscriptionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ServicesViewModel(serviceRepo, subscriptionRepo) as T
}