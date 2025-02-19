package com.example.mangiaebasta.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mangiaebasta.model.MenuDetail
import com.example.mangiaebasta.model.OrderInfo
import com.example.mangiaebasta.model.UserResponse
import com.example.mangiaebasta.utils.formatTimestamp
import java.time.Duration
import java.time.Instant

@Composable
fun OrderDetailsCard(
    orderDetails: OrderInfo?,
    menuDetail: MenuDetail?,
    userResponse: UserResponse,
    navController: NavController
) {
    when {
        orderDetails == null -> {
            // Nessun ordine effettuato
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Nessun ordine effettuato. Inizia ad ordinare!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(
                    onClick = { navController.navigate("menu") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28A745))
                ) {
                    Text("Vai al menu")
                }
            }
        }

        menuDetail == null-> {
            // caricamento in attesa dei dati
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        else -> {
            // dettagli ordine
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MenuImage(
                            user = userResponse,
                            mid = menuDetail.mid,
                            imageVersion = menuDetail.imageVersion,
                            modifier = Modifier.size(80.dp)
                        )
                        // Info ordine
                        Column(
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .weight(1f)
                        ) {
                            Text(
                                text = menuDetail.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = when (orderDetails.status) {
                                    "COMPLETED" -> "COMPLETATO"
                                    else -> "IN CONSEGNA"
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (orderDetails.status == "COMPLETED") Color(0xFF28A745) else Color(0xFFFFA500)
                            )
                            Text(
                                text = if (orderDetails.status == "COMPLETED") {
                                    "Orario di consegna: ${formatTimestamp(orderDetails.deliveryTimestamp!!)}"
                                } else {
                                    "Orario di arrivo previsto: ${formatTimestamp(orderDetails.expectedDeliveryTimestamp!!)}"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // minuti rimanenti
                if (orderDetails.status != "COMPLETED") {
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Minuti\nrimanenti",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Text(
                            text = "${calculateMinutesRemaining(orderDetails)}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF28A745)
                        )
                    }
                }
            }
        }
    }
}

fun calculateMinutesRemaining(orderDetails: OrderInfo): Int {
    return try {
        val timestamp = orderDetails.expectedDeliveryTimestamp
        if (timestamp.isNullOrEmpty()) {
            Log.e("OrderDetails", "Timestamp di consegna mancante")
            return 0
        }

        val expectedDeliveryTime = Instant.parse(timestamp)
        val currentTime = Instant.now()

        Duration.between(currentTime, expectedDeliveryTime).toMinutes().toInt()
    } catch (e: Exception) {
        Log.e("OrderDetails", "Errore nel parsing della data: ${e.message}")
        0
    }
}
