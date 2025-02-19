package com.example.mangiaebasta.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mangiaebasta.model.UserResponse
import com.example.mangiaebasta.model.Location
import com.example.mangiaebasta.model.UserInfo
import com.example.mangiaebasta.repository.UserRepository
import com.example.mangiaebasta.viewmodel.MenuViewModel
import com.example.mangiaebasta.viewmodel.OrderViewModel
import com.example.mangiaebasta.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun MenuDetailsScreen(
    userViewModel: UserViewModel,
    userRepository: UserRepository,
    menuId: Int,
    userResponse: UserResponse,
    userInfo: UserInfo?,
    deliveryLocation: Location,
    onBack: () -> Unit,
    navController: NavController
) {
    val menuViewModel: MenuViewModel = viewModel()
    val orderViewModel: OrderViewModel = viewModel()

    val menuDetail by menuViewModel.menuDetail.collectAsState()
    val orderState by orderViewModel.orderState.collectAsState()
    val errorState by orderViewModel.errorState.collectAsState()
    val isLoading by orderViewModel.isLoading.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(menuId) {
        menuViewModel.fetchMenuDetails(userResponse, menuId, deliveryLocation)
    }

    LaunchedEffect(userResponse) {
        userViewModel.getUserDetails(userRepository, userResponse)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                menuDetail != null -> {
                    MenuImage(userResponse, menuId, menuDetail!!.imageVersion, Modifier.fillMaxWidth().aspectRatio(1f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = menuDetail!!.name, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "€ ${menuDetail!!.price}", color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = menuDetail!!.longDescription)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Luogo di partenza: (${menuDetail!!.location.lat}, ${menuDetail!!.location.lng})")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Tempo di consegna: ${menuDetail!!.deliveryTime} minuti")
                    Spacer(modifier = Modifier.height(16.dp))

                    //logica disattivazione pulante place order
                    val isOrderDisabled = userInfo?.orderStatus == "ON_DELIVERY" || userInfo?.cardNumber == null

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                orderViewModel.placeOrder(menuId, userResponse, deliveryLocation)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28A745)),
                        enabled = !isOrderDisabled
                    ) {
                        Text("Ordina Ora")
                    }

                    if (isOrderDisabled) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Non è possibile ordinare: ${if (userInfo?.orderStatus == "ON_DELIVERY") "ordine in corso" else "profilo incompleto"}",
                            color = Color.Red
                        )
                    }

                    errorState?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = it, color = Color.Red)
                        Log.e("MenuDetailsScreen", "Errore: $it")
                    }

                    //alert quando viene chiamato ordine
                    var showDialog by remember { mutableStateOf(true) }
                    if (showDialog && orderState != null) {
                        AlertDialog(
                            modifier = Modifier.fillMaxWidth(),
                            onDismissRequest = { showDialog = false }, // Chiude il dialog al tap fuori
                            containerColor = Color(0xFFF9F9F9),
                            title = { Text("Ordine effettuato") },
                            text = { Text("Il tuo ordine è stato piazzato con successo!") },
                            dismissButton = {
                                Button(
                                    onClick = { showDialog = false }, // Chiude il dialog premendo "Chiudi"
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                                ) {
                                    Text("Chiudi")
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        showDialog = false
                                        navController.navigate("delivery_state")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28A745))
                                ) {
                                    Text("Visualizza ordine")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
