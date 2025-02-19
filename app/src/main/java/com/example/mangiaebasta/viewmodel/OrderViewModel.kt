package com.example.mangiaebasta.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangiaebasta.model.Location
import com.example.mangiaebasta.model.MenuDetail
import com.example.mangiaebasta.model.UserResponse
import com.example.mangiaebasta.model.OrderInfo
import com.example.mangiaebasta.repository.OrderRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {
    private val orderRepository = OrderRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _orderState = MutableStateFlow<OrderInfo?>(null)
    val orderState: StateFlow<OrderInfo?> = _orderState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    private val _orderDetailsState = MutableStateFlow<OrderInfo?>(null)
    val orderDetailsState: StateFlow<OrderInfo?> = _orderDetailsState

    private val _orderAndMenuState = MutableStateFlow<Pair<OrderInfo?, MenuDetail?>?>(null)
    val orderAndMenuState: StateFlow<Pair<OrderInfo?, MenuDetail?>?> = _orderAndMenuState

    private var isRefreshing = false

    // Funzione per piazzare un ordine
    fun placeOrder(mid: Int, userResponse: UserResponse, deliveryLocation: Location) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                Log.d("OrderViewModel", "Effettuo ordine per menu con mid: $mid")
                val result = orderRepository.placeOrder(mid, userResponse, deliveryLocation)

                if (result != null) {
                    _orderState.value = result
                    _errorState.value = null
                } else {
                    _errorState.value = "Errore nel piazzare l'ordine"
                }
            } catch (e: Exception) {
                Log.e("OrderViewModel", "Errore durante il piazzamento dell'ordine: ${e.message}")
                _errorState.value = "Errore: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Funzione per ottenere i dettagli di un ordine e il menu associato
    fun getOrderAndMenuDetails(userResponse: UserResponse, oid: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                Log.d("OrderViewModel", "Ottenendo i dettagli dell'ordine e del menu per oid: $oid")
                val result = orderRepository.getOrderAndMenuDetails(userResponse, oid)

                if (result.first != null && result.second != null) {
                    _orderAndMenuState.value = result
                    _errorState.value = null
                } else {
                    _errorState.value = "Errore nel recuperare i dettagli dell'ordine e del menu"
                }
            } catch (e: Exception) {
                Log.e("OrderViewModel", "Errore durante il recupero dei dettagli dell'ordine e del menu: ${e.message}")
                _errorState.value = "Errore: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Funzione per avviare l'auto refresh finché non si completa l'ordine
    fun startAutoRefresh(userResponse: UserResponse, oid: Int) {
        isRefreshing = true
        viewModelScope.launch {
            while (isRefreshing) {
                getOrderAndMenuDetails(userResponse, oid) // Aggiorna i dati

                // Verifica lo stato della consegna
                val orderInfo = _orderAndMenuState.value?.first
                if (orderInfo?.status == "COMPLETED") {
                    isRefreshing = false // Ferma l'auto-refresh
                }

                delay(5000)
            }
        }
    }

    // Funzione per fermare l'auto refresh manualmente
    fun stopAutoRefresh() {
        isRefreshing = false
    }

    fun deleteLastOrder(userResponse: UserResponse): Boolean {
        _isLoading.value = true
        var result = false
        viewModelScope.launch {
            try {
                Log.d("OrderViewModel", "Cancello ultimo ordine")
                result = orderRepository.deleteLastOrder(userResponse)
                stopAutoRefresh()
                _errorState.value = null
            } catch (e: Exception) {
                Log.e("OrderViewModel", "Errore durante l'eliminazione dell'ultimo ordine: ${e.message}")
                _errorState.value = "Errore: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
        return result
    }
}
