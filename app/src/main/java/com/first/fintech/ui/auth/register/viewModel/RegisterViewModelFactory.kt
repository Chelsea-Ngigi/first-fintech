package com.first.fintech.ui.auth.register.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.first.fintech.data.repository.auth.AuthRepository


class RegisterViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        RegisterViewModel(repository) as T
}