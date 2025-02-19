package com.example.mangiaebasta.repository

import android.content.Context
import android.util.Log
import com.example.mangiaebasta.model.UserInfo
import com.example.mangiaebasta.model.UserPreferences
import com.example.mangiaebasta.network.CommunicationController
import com.example.mangiaebasta.model.UserResponse

class UserRepository(private val context: Context) {

    // Funzione per ottenere o creare un utente
    suspend fun getOrCreateUser(): UserResponse? {
        val user = UserPreferences.getUser(context)

        return if (user != null) {
            // Se l'utente esiste, lo restituiamo
            Log.d("UserRepository", "Recupero utente dal DataStore - SID: ${user.sid}, UID: ${user.uid}")
            user
        } else {
            Log.d("UserRepository", "Nessun utente trovato nel DataStore, creo nuovo utente...")
            // Altrimenti creiamo un nuovo utente
            val newUser = CommunicationController.createUser()
            if (newUser != null) {
                // Salviamo il nuovo utente nelle preferenze
                UserPreferences.saveUser(context, newUser.sid, newUser.uid)
                Log.d("UserViewModel", "Nuovo utente salvato - SID: ${newUser.sid}, UID: ${newUser.uid}")
                newUser
            } else {
                null
            }
        }
    }

    suspend fun getUserDetails(userResponse: UserResponse): UserInfo? {
        return CommunicationController.getUserDetail(userResponse)
    }

    // Aggiornare i dettagli dell'utente
    suspend fun updateUserDetails(userInfo: UserInfo, userResponse: UserResponse): Boolean {
        return try {
            val success = CommunicationController.updateUserDetails(userResponse, userInfo)
            if (success) {
                Log.d("UserRepository", "Dettagli utente aggiornati con successo")
            } else {
                Log.e("UserRepository", "Impossibile aggiornare dettagli utente")
            }
            success
        } catch (e: Exception) {
            Log.e("UserRepository", "Errore nell'aggiornare dettagli utente: ${e.message}")
            false
        }
    }
}
