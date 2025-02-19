package com.example.mangiaebasta.utils

import java.text.SimpleDateFormat
import java.util.*

fun formatTimestamp(timestamp: String): String {
    return try {
        // Converte la stringa timestamp in un oggetto Date
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(timestamp)
        // Formatta la data in una stringa leggibile
        val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(date!!)
        formattedDate
    } catch (e: Exception) {
        // Gestisce eventuali errori nel formato del timestamp
        e.printStackTrace()
        "Formato data errato"
    }
}
