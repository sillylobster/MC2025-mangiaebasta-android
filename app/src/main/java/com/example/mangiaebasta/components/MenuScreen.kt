package com.example.mangiaebasta.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mangiaebasta.model.Location
import com.example.mangiaebasta.model.Menu
import com.example.mangiaebasta.model.UserResponse
import com.example.mangiaebasta.viewmodel.MenuViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MenuScreen(
    userResponse: UserResponse,
    deliveryLocation: Location?,
    onMenuSelected: (Menu) -> Unit
) {
    if (deliveryLocation == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val menuViewModel: MenuViewModel = viewModel()
    val menuState by menuViewModel.menu.collectAsState()

    LaunchedEffect(deliveryLocation) {
        menuViewModel.fetchMenu(userResponse, deliveryLocation)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Menu nei dintorni",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when {
            menuState == null -> { // Stato di caricamento
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            menuState!!.isEmpty() -> { // Lista vuota
                Text("Nessun menu disponibile.", style = MaterialTheme.typography.bodyLarge)
            }
            else -> { // Dati disponibili
                MenuList(userResponse, menuState!!) { menuItem ->
                    onMenuSelected(menuItem)
                }
            }
        }
    }
}
