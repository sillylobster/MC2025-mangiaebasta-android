package com.example.mangiaebasta.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mangiaebasta.model.Menu
import com.example.mangiaebasta.model.UserResponse

@Composable
fun MenuItem(user: UserResponse, menuItem: Menu, onClick: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            //.height(150.dp)
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            MenuImage(user, menuItem.mid, menuItem.imageVersion, Modifier.size(100.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = menuItem.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "€ ${menuItem.price}", color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = menuItem.shortDescription, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Consegna: ${menuItem.deliveryTime} minuti", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
