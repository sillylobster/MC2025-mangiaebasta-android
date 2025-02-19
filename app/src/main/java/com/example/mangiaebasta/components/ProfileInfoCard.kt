package com.example.mangiaebasta.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mangiaebasta.model.UserInfo
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.Icon


@Composable
fun ProfileInfoCard(userInfo: UserInfo?, handleEdit: () -> Unit) {
    // Verifica se uno dei dati di userInfo è nullo
    val hasMissingData =
            userInfo?.firstName == null ||
            userInfo.lastName == null ||
            userInfo.cardFullName == null ||
            userInfo.cardNumber == null ||
            userInfo.cardCVV == null ||
            userInfo.cardExpireMonth == null ||
            userInfo.cardExpireYear == null

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        if (hasMissingData) {
            // Se ci sono dati mancanti
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Dati mancanti, completa il profilo", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {handleEdit()},
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28A745))
                ) {
                    Text(text = "Completa", color = Color.White)
                }
            }
        } else {
            // Se i dati sono presenti
            Column(modifier = Modifier.padding(16.dp)) {
                // Sezione utente
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "User Icon", tint = Color.Gray)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "Utente", style = MaterialTheme.typography.titleMedium)
                        Text(text = "${userInfo?.firstName} ${userInfo?.lastName}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Sezione carta
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.CreditCard, contentDescription = "Credit Card Icon", tint = Color.Gray)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "Carta", style = MaterialTheme.typography.titleMedium)
                        Text(text = "Nome carta: ${userInfo?.cardFullName}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Numero carta: ${userInfo?.cardNumber}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "CVV: ${userInfo?.cardCVV}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Scadenza: ${userInfo?.cardExpireMonth}/${userInfo?.cardExpireYear}", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                // pulsante modifica profilo
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {handleEdit()},
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28A745))
                ) {
                    Text(text = "Modifica", color = Color.White)
                }
            }
        }
    }
}
