package com.example.mangiaebasta.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mangiaebasta.network.CommunicationController.deleteLastOrder
import com.example.mangiaebasta.viewmodel.UserViewModel
import com.example.mangiaebasta.repository.UserRepository
import com.example.mangiaebasta.viewmodel.OrderViewModel

@Composable
fun ProfileScreen(
    userViewModel: UserViewModel,
    userRepository: UserRepository,
    navController: NavController
) {
    val user by userViewModel.userState.collectAsState()
    val userInfo by userViewModel.userInfoState.collectAsState()

    val orderViewModel: OrderViewModel = viewModel()
    val orderAndMenuDetails by orderViewModel.orderAndMenuState.collectAsState()

    // Stato per gestire la cancellazione dell'ordine
    var isDeletingOrder by remember { mutableStateOf(false) }

    // Stato aggiornato per evitare il refresh non voluto
    val currentUser by rememberUpdatedState(user)

    // Recupera i dettagli utente
    LaunchedEffect(Unit) {
        if (currentUser != null) {
            userViewModel.getUserDetails(userRepository, currentUser!!)
        }
    }

    // Recupera l'ultimo ordine
    LaunchedEffect(userInfo?.lastOid) {
        if (userInfo != null && userInfo!!.lastOid != null) {
            orderViewModel.getOrderAndMenuDetails(currentUser!!, userInfo!!.lastOid!!)
        }
    }

    // Funzione per gestire l'edit del profilo
    fun handleEdit() {
        userInfo?.let {
            navController.navigate("edit_profile")
        }
    }

    // Funzione per gestire la cancellazione dell'ordine
    fun handleDeleteOrder() {
        if (!isDeletingOrder) {
            isDeletingOrder = true

            // Esegui l'eliminazione dell'ordine asincrona
            currentUser?.let { userResponse ->
                orderViewModel.deleteLastOrder(userResponse)
                orderViewModel.stopAutoRefresh()

                // Dopo aver cancellato, naviga e aggiorna lo stato
                navController.navigate("profile") {
                    popUpTo("profile") { inclusive = true }
                }

                // Imposta lo stato per segnare che la cancellazione è completata
                isDeletingOrder = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
//            .statusBarsPadding()
    ) {
        Column (Modifier.padding(16.dp)){
            Text(
                text = "Profilo",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                // Contenuto del profilo
                if (userInfo == null) {
                    // Stato di caricamento
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Dati disponibili
                    Text(
                        text = "Dati Personali",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    ProfileInfoCard(userInfo = userInfo, handleEdit = { handleEdit() })

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Ultimo ordine",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LastOrderCard(
                        user = currentUser!!,
                        userInfo = userInfo!!,
                        menuDetail = orderAndMenuDetails?.second
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { handleDeleteOrder() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC3545))
                    ) {
                        Text("Elimina Ultimo Ordine")
                    }
                }

            }
        }
    }
}
