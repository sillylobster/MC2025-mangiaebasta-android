package com.example.mangiaebasta

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mangiaebasta.components.BottomNavBar
import com.example.mangiaebasta.components.DeliveryStateScreen
import com.example.mangiaebasta.components.EditProfileScreen
import com.example.mangiaebasta.components.MenuDetailsScreen
import com.example.mangiaebasta.components.MenuScreen
import com.example.mangiaebasta.components.ProfileScreen
import com.example.mangiaebasta.model.PagePreferences
import com.example.mangiaebasta.repository.UserRepository
import com.example.mangiaebasta.ui.theme.MangiaEBastaTheme
import com.example.mangiaebasta.viewmodel.LocationViewModel
import com.example.mangiaebasta.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val userRepository by lazy { UserRepository(this) }
    private val userViewModel: UserViewModel by viewModels()
    private val locationViewModel: LocationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate chiamato")
        enableEdgeToEdge()

        userViewModel.getOrCreateUser(userRepository)

        lifecycleScope.launch {
            try {
                val savedPage = PagePreferences.getPageData(this@MainActivity)
                Log.d("NavigationApp", "Recupero pagina salvata: $savedPage")

                setContent {
                    MangiaEBastaTheme {
                        val navController = rememberNavController()
                        val startDestination = when {
                            savedPage.startsWith("details/") -> {
                                val menuId = savedPage.substringAfter("details/")
                                if (menuId.isNotEmpty()) savedPage else "menu"
                            }
                            savedPage in listOf("menu", "delivery_state", "profile") -> savedPage
                            else -> "menu"
                        }
                        NavigationApp(navController, userViewModel, locationViewModel, userRepository, startDestination)
                    }
                }
            } catch (e: Exception) {
                Log.e("NavigationApp", "Errore caricamento pagina salvata: ${e.message}")
                setContent {
                    MangiaEBastaTheme {
                        val navController = rememberNavController()
                        NavigationApp(navController, userViewModel, locationViewModel, userRepository, "menu")
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart chiamato")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop chiamato")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume chiamato")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause chiamato")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy chiamato")
    }
}

@Composable
fun NavigationApp(
    navController: NavHostController,
    userViewModel: UserViewModel,
    locationViewModel: LocationViewModel,
    userRepository: UserRepository,
    startDestination: String
) {
    val user by userViewModel.userState.collectAsState()
    val location by locationViewModel.locationState.collectAsState()
    var hasPermission by remember { mutableStateOf(locationViewModel.checkAndRequestLocationPermission()) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) {
            Log.d("MainActivity", "Permesso concesso. Calcolo posizione...")
            locationViewModel.getCurrentLocation()
        } else {
            Log.d("MainActivity", "Permesso negato. Non è stato possibile calcolare la posizione.")
        }
    }

    // Controllo permessi per la posizione
    LaunchedEffect(Unit) {
        if (hasPermission) {
            locationViewModel.getCurrentLocation()
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            val baseRoute = destination.route ?: "menu"
            val currentPage = when {
                baseRoute == "details/{menuId}" -> {
                    val menuId = arguments?.getString("menuId")
                    if (menuId != null) "details/$menuId" else "menu"
                }
                else -> baseRoute
            }
            PagePreferences.savePageData(navController.context, currentPage)
            Log.d("NavigationApp", "Pagina salvata: $currentPage")
        }
    }

    Scaffold(bottomBar = { BottomNavBar(navController) }) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        if (user == null || location == null) {
            Column(
                modifier = Modifier.fillMaxSize().then(modifier),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Text("Caricamento dati...")
            }
            return@Scaffold
        }

        NavHost(navController, startDestination, modifier) {
            composable("menu") {
                MenuScreen(user!!, location) { menu ->
                    navController.navigate("details/${menu.mid}")
                }
            }
            composable("details/{menuId}") { backStackEntry ->
                val menuId = backStackEntry.arguments?.getString("menuId")?.toIntOrNull() ?: 0
                val userInfo by userViewModel.userInfoState.collectAsState()
                if (menuId == 0) {
                    navController.navigate("menu")
                } else {
                    MenuDetailsScreen(userViewModel, userRepository, menuId, user!!, userInfo, location!!, onBack = {
                        navController.navigate("menu")
                    }, navController)
                }
            }
            composable("delivery_state") {
                DeliveryStateScreen(user!!, navController, userRepository)
            }
            composable("profile") {
                ProfileScreen(userViewModel, userRepository, navController)
            }
            composable("edit_profile") {
                val userInfo by userViewModel.userInfoState.collectAsState()
                if (userInfo != null) {
                    EditProfileScreen (
                        userInfo = userInfo,
                        handleInputChange = userViewModel::updateUserField,
                        handleSave = {
                            userViewModel.saveUserData(userRepository)
                            navController.popBackStack() },
                        handleCancel = { navController.popBackStack() })
                } else {
                    Text("Errore nel recupero dei dati utente")
                }
            }
        }
    }
}
