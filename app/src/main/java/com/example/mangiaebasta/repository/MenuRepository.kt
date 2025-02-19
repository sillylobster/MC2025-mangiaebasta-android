package com.example.mangiaebasta.repository

import com.example.mangiaebasta.model.Location
import com.example.mangiaebasta.model.Menu
import com.example.mangiaebasta.model.MenuDetail
import com.example.mangiaebasta.model.UserResponse
import com.example.mangiaebasta.network.CommunicationController

class MenuRepository {
    suspend fun getMenu(userResponse: UserResponse, deliveryLocation: Location): List<Menu>? {
        return try {
            CommunicationController.getMenu(userResponse, deliveryLocation)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getMenuDetails(userResponse: UserResponse, mid: Int, deliveryLocation: Location): MenuDetail? {
        return try {
            CommunicationController.getMenuDetails(userResponse, mid, deliveryLocation)
        } catch (e: Exception) {
            null
        }
    }
}
