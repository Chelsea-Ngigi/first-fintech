package com.first.fintech.ui.subscriptions.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.first.fintech.data.repository.subscriptions.SubscriptionRepository


class SubscriptionsViewModelFactory(
    private val repository: SubscriptionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        SubscriptionsViewModel(repository) as T
}