package com.example.mangiaebasta.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangiaebasta.model.UserInfo
import com.example.mangiaebasta.model.UserResponse
import com.example.mangiaebasta.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val _userState = MutableStateFlow<UserResponse?>(null)
    val userState = _userState.asStateFlow()

    private val _userInfoState = MutableStateFlow<UserInfo?>(null)
    val userInfoState = _userInfoState.asStateFlow()

    // Ottenere o creare un utente e caricare i dettagli
    fun getOrCreateUser(userRepository: UserRepository) {
        viewModelScope.launch {
            val user = userRepository.getOrCreateUser()
            _userState.value = user
        }
    }

    // Ottenere dettagli utente
    fun getUserDetails(userRepository: UserRepository, user: UserResponse) {
        viewModelScope.launch {
            val userDetails = userRepository.getUserDetails(user)
            if (userDetails != null) {
                _userInfoState.value = userDetails
                //Log.d("UserViewModel", "Dettagli utente ottenuti: $userDetails")
            } else {
                Log.e("UserViewModel", "Errore nel caricare i dettagli utente")
            }
        }
    }

    // Modifica temporanea di un campo del profilo utente
    fun updateUserField(field: String, value: String) {
        _userInfoState.value = _userInfoState.value?.let {
            when (field) {
                "firstName" -> it.copy(firstName = value)
                "lastName" -> it.copy(lastName = value)
                "cardFullName" -> it.copy(cardFullName = value)
                "cardNumber" -> it.copy(cardNumber = value)
                "cardExpireMonth" -> it.copy(cardExpireMonth = value.toIntOrNull())
                "cardExpireYear" -> it.copy(cardExpireYear = value.toIntOrNull())
                "cardCVV" -> it.copy(cardCVV = value)
                else -> it
            }
        }
    }


    // Salvare le modifiche al profilo utente
    fun saveUserData(userRepository: UserRepository) {

        viewModelScope.launch {
            _userInfoState.value?.let { userInfo ->
                val success = userRepository.updateUserDetails(userInfo, userState.value!!)
                if (success) {
                    Log.d("UserViewModel", "Dati utente salvati con successo")
                } else {
                    Log.e("UserViewModel", "Errore nel salvataggio dei dati utente")
                }
            }
        }
    }
}
