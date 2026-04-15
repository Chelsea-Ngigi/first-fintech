package com.first.fintech.ui.subscriptions.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.first.fintech.util.Event

import androidx.lifecycle.viewModelScope
import com.first.fintech.data.model.subscriptions.SubscriptionsResponse

import com.first.fintech.data.repository.subscriptions.SubscriptionRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch

class SubscriptionsViewModel(
    private val repository: SubscriptionRepository
) : ViewModel() {

    private val _subscriptions = MutableLiveData<Event<Result<SubscriptionsResponse>>>()
    val subscriptions: LiveData<Event<Result<SubscriptionsResponse>>> = _subscriptions

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadSubscriptions(email: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getSubscriptions(email)
                Log.d(" Subscriptions API", response.body().toString())
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.data != null) {
                        _subscriptions.postValue(Event(Result.success(body)))
                    } else {
                        _subscriptions.postValue(Event(Result.failure(Exception(body.message ?: "Failed to fetch subscriptions"))))
                    }
                } else {
                    val gson = Gson()

                    val errorJson = response.errorBody()?.string()
                    val errorResponse = try {
                        gson.fromJson(errorJson, SubscriptionsResponse::class.java)
                    } catch (e: Exception) {
                        null
                    }

                    if (errorResponse != null) {
                        _subscriptions.postValue(
                            Event(Result.success(errorResponse))
                        )
                    } else {
                        _subscriptions.postValue(
                            Event(Result.failure(Exception("HTTP ${response.code()}")))
                        )
                    }

                }
            } catch (e: Exception) {
                _subscriptions.postValue(Event(Result.failure(e)))
            }
            _isLoading.postValue(false)
        }
    }
}