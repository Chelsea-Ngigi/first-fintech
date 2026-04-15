package com.first.fintech.ui.auth.register.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.first.fintech.data.model.register.RegisterRequest
import com.first.fintech.data.model.register.RegisterResponse
import com.first.fintech.data.repository.auth.AuthRepository
import com.first.fintech.util.Event

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<Event<Result<RegisterResponse>>>()
    val registerResult: LiveData<Event<Result<RegisterResponse>>> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(fullName: String, email: String, msisdn: String, credentials: String) {
        if (!validateInputs(fullName, email, msisdn, credentials)) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = RegisterRequest(fullName, email, msisdn, credentials)
                val response = repository.register(request)
                val body = response.body()

                if (response.isSuccessful && body != null) {

                    // Validation errors from backend
                    if (!body.errors.isNullOrEmpty()) {
                        val errorMessage = body.errors.joinToString("\n") {
                            "${it.fieldName}: ${it.message}"
                        }
                        _registerResult.postValue(
                            Event(Result.failure(Exception(errorMessage)))
                        )
                    } else {
                        // Successful registration
                        _registerResult.postValue(Event(Result.success(body)))
                    }

                } else {
                    // Http error
                    _registerResult.postValue(
                        Event(
                            Result.failure(
                                Exception("HTTP ${response.code()}: ${response.message()}")
                            )
                        )
                    )
                }

            } catch (e: Exception) {
                _registerResult.postValue(Event(Result.failure(e)))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private val _validationError = MutableLiveData<String?>()
    val validationError: LiveData<String?> = _validationError

    private fun validateInputs(
        fullName: String, email: String,
        msisdn: String, credentials: String
    ): Boolean {
        return when {
            fullName.isBlank() -> {
                _validationError.value = "Name is required"
                false
            }
            email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _validationError.value = "Valid email is required"
                false
            }
            msisdn.isBlank() || msisdn.length < 10 -> {
                _validationError.value = "Valid phone number is required"
                false
            }
            credentials.isBlank() || credentials.length < 8 -> {
                _validationError.value = "Password must be at least 8 characters"
                false
            }
            else -> true
        }
    }
}