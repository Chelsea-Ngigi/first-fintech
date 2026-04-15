package com.first.fintech.ui.auth.login.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.first.fintech.data.repository.auth.AuthRepository
import com.first.fintech.util.Event

import androidx.lifecycle.viewModelScope
import com.first.fintech.data.model.login.LoginRequest
import com.first.fintech.data.model.login.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Event<Result<LoginResponse>>>()
    val loginResult: LiveData<Event<Result<LoginResponse>>> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _validationError = MutableLiveData<String?>()
    val validationError: LiveData<String?> = _validationError

    fun login(email: String, password: String) {
        if (!validateInputs(email, password)) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = LoginRequest(email, password)
                val response = repository.login(request)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.data != null) {
                        _loginResult.postValue(Event(Result.success(body)))
                    } else {
                        _loginResult.postValue(Event(Result.failure(Exception(body.message))))
                    }
                } else {
                    _loginResult.postValue(
                        Event(Result.failure(Exception("HTTP ${response.code()}")))
                    )
                }
            } catch (e: Exception) {
                _loginResult.postValue(Event(Result.failure(e)))
            }
            _isLoading.postValue(false)
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        return when {
            email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _validationError.value = "Valid email is required"
                false
            }
            password.isBlank() -> {
                _validationError.value = "Password is required"
                false
            }
            else -> true
        }
    }
}