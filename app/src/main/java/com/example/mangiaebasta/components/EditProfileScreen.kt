package com.example.mangiaebasta.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mangiaebasta.model.UserInfo

@Composable
fun EditProfileScreen(
    userInfo: UserInfo?,
    handleInputChange: (String, String) -> Unit,
    handleSave: () -> Unit,
    handleCancel: () -> Unit,
) {
    if (userInfo == null) {
        Text("Errore: dati utente non disponibili")
        return
    }

    val context = LocalContext.current

    //validazione form
    fun validateForm(): Boolean {
        if (userInfo.cardCVV?.length != 3) {
            Toast.makeText(context, "Il CVV deve essere composto da 3 cifre", Toast.LENGTH_SHORT).show()
            return false
        }
        if (userInfo.cardNumber?.length != 16) {
            Toast.makeText(context, "Il numero di carta deve essere di 16 cifre", Toast.LENGTH_SHORT).show()
            return false
        }
        val month = userInfo.cardExpireMonth
        if (month == null || month !in 1..12) {
            Toast.makeText(context, "Il mese di scadenza deve essere tra 01 e 12", Toast.LENGTH_SHORT).show()
            return false
        }
        val year = userInfo.cardExpireYear
        if (year == null || year.toString().length != 4) {
            Toast.makeText(context, "L'anno di scadenza deve avere 4 cifre", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Modifica Profilo",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = userInfo.firstName ?: "",
            onValueChange = { handleInputChange("firstName", it.take(15)) },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = userInfo.lastName ?: "",
            onValueChange = { handleInputChange("lastName", it.take(15)) },
            label = { Text("Cognome") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = userInfo.cardFullName ?: "",
            onValueChange = { handleInputChange("cardFullName", it.take(31)) },
            label = { Text("Nome carta") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = userInfo.cardNumber ?: "",
            onValueChange = { handleInputChange("cardNumber", it.take(16)) },
            label = { Text("Numero carta") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = userInfo.cardCVV ?: "",
            onValueChange = { handleInputChange("cardCVV", it.take(3)) },
            label = { Text("CVV") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = userInfo.cardExpireMonth?.toString() ?: "",
                onValueChange = { handleInputChange("cardExpireMonth", it.take(2)) },
                label = { Text("Mese") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = userInfo.cardExpireYear?.toString() ?: "",
                onValueChange = { handleInputChange("cardExpireYear", it.take(4)) },
                label = { Text("Anno") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    handleCancel()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Annulla", color = Color.White)
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    if (validateForm()) {
                        handleSave()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Salva", color = Color.White)
            }
        }
    }
}