package com.example.mangiaebasta.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mangiaebasta.R
import com.example.mangiaebasta.model.UserResponse
import com.example.mangiaebasta.repository.UserRepository
import com.example.mangiaebasta.viewmodel.OrderViewModel
import com.example.mangiaebasta.viewmodel.UserViewModel
import com.mapbox.geojson.MultiPoint
import com.mapbox.geojson.Point
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.plugin.viewport.data.OverviewViewportStateOptions
import com.mapbox.maps.plugin.viewport.viewport

@Composable
fun DeliveryStateScreen(
    userResponse: UserResponse,
    navController: NavController,
    userRepository: UserRepository
) {
    val userViewModel: UserViewModel = viewModel()
    val userInfo by userViewModel.userInfoState.collectAsState()

    LaunchedEffect(Unit) {
        userResponse.let {
            userViewModel.getUserDetails(userRepository, it)
        }
    }

    val orderViewModel: OrderViewModel = viewModel()
    val orderAndMenuDetails by orderViewModel.orderAndMenuState.collectAsState()
    val orderInfo = orderAndMenuDetails?.first
    val menuDetail = orderAndMenuDetails?.second

    // Flag per gestire la visibilità della schermata
    var isVisible by remember { mutableStateOf(false) }

    // Avvia e ferma il refresh automatico in base allo stato dell'ordine
    LaunchedEffect(userInfo, orderAndMenuDetails?.first?.status, isVisible) {
        if (isVisible) {
            userInfo?.let { user ->
                val oid = user.lastOid
                val deliveryStatus = orderAndMenuDetails?.first?.status

                if (oid != null) {
                    orderViewModel.getOrderAndMenuDetails(userResponse, oid)

                    if (deliveryStatus == "ON_DELIVERY") {
                        orderViewModel.startAutoRefresh(userResponse, oid)
                    } else if (deliveryStatus == "COMPLETED" || deliveryStatus == "CANCELLED") {
                        Log.d("DeliveryStateScreen", "Ordine comletato o cancellato, fermo auto-refresh.")
                        orderViewModel.stopAutoRefresh()
                    }
                }
            }
        }
    }

    // Gestisci quando la schermata viene visualizzata o nascosta
    DisposableEffect(Unit) {
        onDispose {
            isVisible = false
            orderViewModel.stopAutoRefresh()
        }
        // Rende visibile la schermata quando viene caricata
        isVisible = true
        onDispose {
            isVisible = false
            orderViewModel.stopAutoRefresh()
        }
    }

    val mapViewportState = rememberMapViewportState()

    // focus sui punti in caso di ordine in corso e completato
    LaunchedEffect(orderInfo, menuDetail) {
        if (orderInfo != null) {
            val deliveryPoint = Point.fromLngLat(orderInfo.deliveryLocation.lng, orderInfo.deliveryLocation.lat)

            if (orderInfo.status == "COMPLETED") {
                mapViewportState.transitionToOverviewState(
                    overviewViewportStateOptions = OverviewViewportStateOptions.Builder()
                        .geometry(deliveryPoint)
                        .padding(EdgeInsets(100.0, 100.0, 100.0, 100.0))
                        .maxZoom(17.0)
                        .build()
                )
            } else if (menuDetail != null) {
                val restaurantPoint = Point.fromLngLat(menuDetail.location.lng, menuDetail.location.lat)

                mapViewportState.transitionToOverviewState(
                    overviewViewportStateOptions = OverviewViewportStateOptions.Builder()
                        .geometry(MultiPoint.fromLngLats(listOf(deliveryPoint, restaurantPoint)))
                        .padding(EdgeInsets(100.0, 100.0, 100.0, 100.0))
                        .maxZoom(15.0)
                        .build()
                )
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Stato consegna",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(16.dp)
        )

        OrderDetailsCard(
            orderDetails = orderInfo,
            menuDetail = menuDetail,
            userResponse = userResponse,
            navController = navController
        )

        // mappa
        Box(modifier = Modifier.weight(1f)) {
            MapboxMap(
                Modifier.fillMaxSize(),
                mapViewportState = mapViewportState
            ) {
                MapEffect(Unit) { mapView ->
                    mapView.location.updateSettings {
                        locationPuck = createDefault2DPuck(withBearing = true)
                        puckBearingEnabled = true
                        puckBearing = PuckBearing.HEADING
                        enabled = true
                    }

                    if (orderInfo == null) {
                        val followPuckState = mapView.viewport.makeFollowPuckViewportState(
                            FollowPuckViewportStateOptions.Builder()
                                .pitch(0.0)
                                .build()
                        )
                        mapView.viewport.transitionTo(followPuckState)
                    }
                }

                val userMarker = rememberIconImage(key = "user_pin", painter = painterResource(id = R.drawable.user_pin))
                val droneMarker = rememberIconImage(key = "drone_pin", painter = painterResource(id = R.drawable.drone_pin))
                val restaurantMarker = rememberIconImage(key = "restaurant_pin", painter = painterResource(id = R.drawable.restaurant_pin))

                if (orderInfo != null) {
                    val deliveryStatus = orderInfo.status
                    val userLocation = orderInfo.deliveryLocation.let {
                        Point.fromLngLat(it.lng, it.lat)
                    }

                    if (deliveryStatus == "COMPLETED") {
                        PointAnnotation(point = userLocation) {
                            iconImage = droneMarker
                            textField = "Consegnato"
                            iconSize = 2.0
                        }
                    } else if (menuDetail != null) {
                        val droneLocation = orderInfo.currentPosition.let {
                            Point.fromLngLat(it.lng, it.lat)
                        }
                        val menuLocation = menuDetail.location.let {
                            Point.fromLngLat(it.lng, it.lat)
                        }

                        PointAnnotation(point = userLocation) {
                            iconImage = userMarker
                            textField = "Utente"
                            iconSize = 2.0
                        }

                        PointAnnotation(point = droneLocation) {
                            iconImage = droneMarker
                            textField = "Drone"
                            iconSize = 2.0
                        }

                        PointAnnotation(point = menuLocation) {
                            iconImage = restaurantMarker
                            textField = "Ristorante"
                            iconSize = 2.0
                        }

                        PolylineAnnotation(points = listOf(droneLocation, userLocation)) {
                            lineColor = Color.Gray
                            lineWidth = 3.0
                        }
                    }
                }
            }

            // Pulsanti mappa
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FloatingActionButton(
                        elevation = FloatingActionButtonDefaults.elevation(4.dp),
                        containerColor = Color(0xFFF9F9F9),
                        onClick = { mapViewportState.easeTo(cameraOptions { zoom(mapViewportState.cameraState!!.zoom + 1) }) }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Zoom in")
                    }

                    FloatingActionButton(
                        elevation = FloatingActionButtonDefaults.elevation(4.dp),
                        containerColor = Color(0xFFF9F9F9),
                        onClick = { mapViewportState.easeTo(cameraOptions { zoom(mapViewportState.cameraState!!.zoom - 1) }) }
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Zoom out")
                    }

                    FloatingActionButton(
                        elevation = FloatingActionButtonDefaults.elevation(4.dp),
                        containerColor = Color(0xFFF9F9F9),
                        onClick = {
                            mapViewportState.transitionToFollowPuckState(
                                followPuckViewportStateOptions = FollowPuckViewportStateOptions.Builder()
                                    .pitch(0.0)
                                    .build()
                            )
                        }
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = "Center on location")
                    }
                }

            }
        }
    }
}
