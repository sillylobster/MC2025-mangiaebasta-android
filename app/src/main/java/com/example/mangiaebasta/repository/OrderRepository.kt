package com.example.mangiaebasta.repository

import android.util.Log
import com.example.mangiaebasta.model.OrderInfo
import com.example.mangiaebasta.model.UserResponse
import com.example.mangiaebasta.model.Location
import com.example.mangiaebasta.model.MenuDetail
import com.example.mangiaebasta.network.CommunicationController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderRepository() {

    // Funzione per piazzare un ordine
    suspend fun placeOrder(mid: Int, userResponse: UserResponse, deliveryLocation: Location): OrderInfo? {
        return try {
            withContext(Dispatchers.IO) {
                CommunicationController.placeOrder(mid, userResponse, deliveryLocation)
            }
        } catch (e: Exception) {
            null
        }
    }

    // Funzione per ottenere i dettagli di un ordine
    suspend fun getOrderDetails(userResponse: UserResponse, oid: Int): OrderInfo? {
        return try {
            withContext(Dispatchers.IO) {
                CommunicationController.getOrderDetails(userResponse, oid)
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Errore durante il recupero dei dettagli dell'ordine: ${e.message}")
            null
        }
    }

    // Funzione per ottenere dettagli di un ordine e del menu associato
    suspend fun getOrderAndMenuDetails(userResponse: UserResponse, oid: Int): Pair<OrderInfo?, MenuDetail?> {
        return try {
            withContext(Dispatchers.IO) {
                val order = CommunicationController.getOrderDetails(userResponse, oid)
                val menu = CommunicationController.getMenuDetails(userResponse,order!!.mid, order.deliveryLocation)
                order to menu
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Errore durante il recupero dei dettagli dell'ordine: ${e.message}")
            null to null
        }
    }

    suspend fun deleteLastOrder(userResponse: UserResponse): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                CommunicationController.deleteLastOrder(userResponse)
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Errore durante l'eliminazione dell'ultimo ordine: ${e.message}")
            false
        }
    }
}
