package com.example.mangiaebasta.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangiaebasta.model.Location
import com.example.mangiaebasta.model.Menu
import com.example.mangiaebasta.model.MenuDetail
import com.example.mangiaebasta.model.UserResponse
import com.example.mangiaebasta.repository.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MenuViewModel : ViewModel() {
    private val menuRepository = MenuRepository()

    private val _menu = MutableStateFlow<List<Menu>?>(null)
    val menu: StateFlow<List<Menu>?> = _menu

    private val _menuDetail = MutableStateFlow<MenuDetail?>(null)
    val menuDetail: StateFlow<MenuDetail?> = _menuDetail

    fun fetchMenu(userResponse: UserResponse, deliveryLocation: Location) {
        viewModelScope.launch {
            try {
                val fetchedMenu = menuRepository.getMenu(userResponse, deliveryLocation)
                _menu.value = fetchedMenu
            } catch (e: Exception) {
                Log.e("MenuViewModel", "Errore durante il recupero del menu", e)
            }
        }
    }

    fun fetchMenuDetails(userResponse: UserResponse, mid: Int, deliveryLocation: Location) {
        viewModelScope.launch {
            try {
                val details = menuRepository.getMenuDetails(userResponse, mid, deliveryLocation)
                _menuDetail.value = details
            } catch (e: Exception) {
                Log.e("MenuViewModel", "Errore nel recupero dei dettagli per il menu con id: $mid", e)
            }
        }
    }
}


