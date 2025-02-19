package com.example.mangiaebasta.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangiaebasta.model.MenuImageEntity
import com.example.mangiaebasta.model.UserResponse
import com.example.mangiaebasta.repository.MenuImageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MenuImageViewModel(private val menuImageRepository: MenuImageRepository) : ViewModel() {

    private val _menuImageState = MutableStateFlow<MenuImageEntity?>(null)
    val menuImageState = _menuImageState.asStateFlow()

    // Funzione per svuotare la tabella
    fun clearMenuImages() {
        viewModelScope.launch {
            menuImageRepository.clearAllMenuImages()
        }
    }

    // Recupera l'immagine del menu dal DB locale o la scarica se non esiste
    fun loadMenuImage(user: UserResponse, mid: Int, imageVersion: Int) {
        viewModelScope.launch {
            val cachedImage = menuImageRepository.getMenuImage(mid, imageVersion)

            if (cachedImage != null) {
                _menuImageState.value = cachedImage
                Log.d("MenuImageViewModel", "Immagine caricata dal db con mid: $mid e imageVersion: $imageVersion")
            } else {
                val newImage = menuImageRepository.fetchAndSaveImage(user, mid, imageVersion)
                _menuImageState.value = newImage
                Log.d("MenuImageViewModel", "Immagine scaricata dal server con mid: $mid e imageVersion: $imageVersion")
            }
        }
    }
}
