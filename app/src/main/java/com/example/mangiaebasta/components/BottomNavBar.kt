package com.example.mangiaebasta.components

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BottomNavBar(
    navController: NavController
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        listOf(
            "Menu" to Icons.Filled.Restaurant,
            "Stato consegna" to Icons.Filled.Map,
            "Profilo" to Icons.Filled.Person
        ).forEach { (item, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = item) },
                label = { Text(item.replaceFirstChar { it.uppercase() }) },
                selected = false,
                onClick = {
                    when (item) {
                        "Menu" -> navController.navigate("menu")
                        "Stato consegna" -> navController.navigate("delivery_state")
                        "Profilo" -> navController.navigate("profile")
                    }
                }
            )
        }
    }
}
