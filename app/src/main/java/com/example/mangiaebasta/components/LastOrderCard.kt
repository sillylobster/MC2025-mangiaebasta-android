package com.example.mangiaebasta.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mangiaebasta.model.MenuDetail
import com.example.mangiaebasta.model.UserInfo
import com.example.mangiaebasta.model.UserResponse

@Composable
fun LastOrderCard(
    user: UserResponse,
    userInfo: UserInfo,
    menuDetail: MenuDetail?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        if (userInfo.lastOid == null) {
            // Caso in cui non ci sono ordini
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Nessun ordine effettuato",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        } else if (menuDetail == null) {
            // Caso in cui i dati del menu non sono ancora disponibili
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            // contenuto card con dati pronti
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //immagine
                menuDetail.let {
                    MenuImage(
                        user = user,
                        mid = it.mid,
                        imageVersion = it.imageVersion,
                        modifier = Modifier.size(80.dp)
                    )
                }

                // Informazioni ordine
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                ) {
                    Text(
                        text = menuDetail.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Text(
                        text = menuDetail.shortDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "€${menuDetail.price}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(end = 16.dp)
                        )

                        Text(
                            text = when (userInfo.orderStatus) {
                                "COMPLETED" -> "COMPLETATO"
                                else -> "IN CONSEGNA"
                            },
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = if (userInfo.orderStatus == "COMPLETED") Color(0xFF28A745) else Color(0xFFFFA500)
                        )
                    }
                }
            }
        }
    }
}
