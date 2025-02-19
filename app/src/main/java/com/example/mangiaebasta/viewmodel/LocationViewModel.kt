package com.example.mangiaebasta.viewmodel

import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangiaebasta.model.Location
import com.example.mangiaebasta.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LocationRepository(application)

    private val _locationState = MutableStateFlow<Location?>(null)
    val locationState: StateFlow<Location?> = _locationState

    fun checkAndRequestLocationPermission(): Boolean {
        val context = getApplication<Application>().applicationContext
        return ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getCurrentLocation() {
        Log.d("LocationViewModel", "Richiesta della posizione attuale...")
        if (checkAndRequestLocationPermission()) {
            viewModelScope.launch {
                val location = repository.getCurrentLocation()
                location?.let {
                    Log.d("LocationViewModel", "Posizione ottenuta: Lat: ${it.lat}, Lng: ${it.lng}")
                } ?: Log.d("LocationViewModel", "Errore nel recupero della posizione")
                _locationState.value = location
            }
        } else {
            Log.d("LocationViewModel", "Permsso non concesso, impossibile ottenere la posizione.")
        }
    }
}
