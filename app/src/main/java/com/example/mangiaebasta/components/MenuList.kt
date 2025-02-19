package com.example.mangiaebasta.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.example.mangiaebasta.model.Menu
import com.example.mangiaebasta.model.UserResponse

@Composable
fun MenuList(
    user: UserResponse,
    menuItems: List<Menu>,
    onMenuSelected: (Menu) -> Unit
) {
    LazyColumn {
        items(menuItems) { menuItem ->
            MenuItem(user, menuItem = menuItem, onClick = {
                onMenuSelected(menuItem)  // Passa il menuItem
            })
        }
    }
}

