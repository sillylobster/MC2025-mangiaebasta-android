package com.example.mangiaebasta.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mangiaebasta.model.MenuDatabase
import com.example.mangiaebasta.model.UserResponse
import com.example.mangiaebasta.repository.MenuImageRepository
import com.example.mangiaebasta.viewmodel.MenuImageViewModel

@Composable
fun MenuImage(user: UserResponse, mid: Int, imageVersion: Int, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val menuImageDao = MenuDatabase.getDatabase(context).menuImageDao()
    val menuImageRepository = MenuImageRepository(menuImageDao)

    val menuImageViewModel = remember(mid, imageVersion) {
        MenuImageViewModel(menuImageRepository)
    }

    val imageState by menuImageViewModel.menuImageState.collectAsState()
    var errorState by remember { mutableStateOf(false) }

    LaunchedEffect(user, mid, imageVersion) {
        menuImageViewModel.loadMenuImage(user, mid, imageVersion)
        errorState = false
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (imageState == null) {
            CircularProgressIndicator()
        } else {
            imageState?.let { menuImage ->
                val bitmap = decodeBase64Image(menuImage.imageBase64)

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Menu Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                    )
                } else {
                    errorState = true
                    CircularProgressIndicator()
                }
            }
        }
    }
}

private fun decodeBase64Image(base64String: String?): Bitmap? {
    return try {
        base64String?.replace("\\s".toRegex(), "")?.takeIf { it.isNotEmpty() }?.let {
            val byteArray = Base64.decode(it, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }
    } catch (e: IllegalArgumentException) {
        Log.e("MenuImage", "Errore nella decodifica Base64: ${e.message}")
        null
    }
}
