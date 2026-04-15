package com.first.fintech.ui.services.viewModel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.first.fintech.util.Event

import androidx.lifecycle.viewModelScope
import com.first.fintech.data.model.services.ServicesResponse
import com.first.fintech.data.model.subscriptions.subscribe.SubscribeRequest
import com.first.fintech.data.model.subscriptions.subscribe.SubscribeResponse
import com.first.fintech.data.repository.services.ServiceRepository
import com.first.fintech.data.repository.subscriptions.SubscriptionRepository
import kotlinx.coroutines.launch

class ServicesViewModel(
    private val serviceRepo: ServiceRepository,
    private val subscriptionRepo: SubscriptionRepository
) : ViewModel() {

    private val _services = MutableLiveData<Event<Result<ServicesResponse>>>()
    val services: LiveData<Event<Result<ServicesResponse>>> = _services

    private val _subscribeResult = MutableLiveData<Event<Result<SubscribeResponse>>>()
    val subscribeResult: LiveData<Event<Result<SubscribeResponse>>> = _subscribeResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadServices() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = serviceRepo.getServices()
                Log.d(" Services API", response.body().toString())
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.data != null) {
                        _services.postValue(Event(Result.success(body)))
                    } else {
                        _services.postValue(Event(Result.failure(Exception(body.message ?: "Failed to fetch services"))))
                    }
                } else {
                    _services.postValue(
                        Event(Result.failure(Exception("HTTP ${response.code()}")))
                    )
                }
            } catch (e: Exception) {
                _services.postValue(Event(Result.failure(e)))
            }
            _isLoading.postValue(false)
        }
    }

    fun subscribe(subscriberEmail: String, serviceName: String, amountPaid: Double) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = SubscribeRequest(subscriberEmail, serviceName, amountPaid)
                val response = subscriptionRepo.subscribe(request)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    _subscribeResult.postValue(Event(Result.success(body)))
                } else {
                    val errorMessage = response.body()?.message
                        ?: "HTTP ${response.code()}: ${response.message()}"

                    _subscribeResult.postValue(
                        Event(Result.failure(Exception(errorMessage)))
                    )
                }
            } catch (e: Exception) {
                _subscribeResult.postValue(Event(Result.failure(e)))
            }
            _isLoading.postValue(false)
        }
    }
}