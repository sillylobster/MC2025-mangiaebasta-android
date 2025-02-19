package com.example.mangiaebasta.repository

import com.example.mangiaebasta.model.MenuImageDao
import com.example.mangiaebasta.model.MenuImageEntity
import com.example.mangiaebasta.model.UserResponse
import com.example.mangiaebasta.network.CommunicationController

class MenuImageRepository(private val menuImageDao: MenuImageDao) {

    // Recupera l'immagine dal DB o ritorna null se non esiste
    suspend fun getMenuImage(mid: Int, imageVersion: Int): MenuImageEntity? {
        return menuImageDao.getMenuImage(mid)?.takeIf { it.imageVersion == imageVersion }
    }

    // Recupera e salva un'immagine dal server
    suspend fun fetchAndSaveImage(user: UserResponse, mid: Int, imageVersion: Int): MenuImageEntity? {
        try {
            val imageData = CommunicationController.getMenuImage(user, mid)
            if (imageData != null) {
                val base64String = imageData.base64
                val newImage = MenuImageEntity(mid, base64String, imageVersion)
                menuImageDao.insertMenuImage(newImage)
                return newImage
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    // Svuota tutte le immagini
    suspend fun clearAllMenuImages() {
        menuImageDao.deleteAllMenuImages()
    }
}
