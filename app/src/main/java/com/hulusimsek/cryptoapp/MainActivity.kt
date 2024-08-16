package com.hulusimsek.cryptoapp

import BottomNavSwipeScreen
import CryptoDetailScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hulusimsek.cryptoapp.ui.theme.CryptoAppTheme
import com.hulusimsek.cryptoapp.view.CryptoListScreen
import com.hulusimsek.cryptoapp.viewmodel.CryptoListViewModel
import com.hulusimsek.cryptoapp.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptoAppTheme {
                val navController = rememberNavController()
                val mainViewModel: MainViewModel = viewModel()

                Scaffold(
                    bottomBar = {
                        BottomNavSwipeScreen(viewModel = mainViewModel, navController = navController)
                    }
                ) { innerPadding ->
                    NavHost(navController = navController, startDestination = "crypto_list_screen", Modifier.padding(innerPadding)) {
                        composable("crypto_list_screen") {
                            CryptoListScreen(navController = navController)
                        }
                        composable("crypto_detail_screen/{cryptoId}/{cryptoPrice}") { backStackEntry ->
                            val cryptoId = backStackEntry.arguments?.getString("cryptoId") ?: ""
                            val cryptoPrice = backStackEntry.arguments?.getString("cryptoPrice") ?: ""
                            CryptoDetailScreen(id = cryptoId, price = cryptoPrice, navController = navController)
                        }
                    }
                }
            }
        }
    }
}
