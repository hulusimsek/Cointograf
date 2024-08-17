package com.hulusimsek.cryptoapp.view

import CryptoDetailScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "crypto_list_screen") {
        composable("crypto_list_screen") {
            CryptoListScreen(navController = navController)
        }
        composable("crypto_detail_screen/{cryptoId}") { backStackEntry ->
            val cryptoId = backStackEntry.arguments?.getString("cryptoId") ?: ""
            CryptoDetailScreen(id = cryptoId, navController = navController)
        }
    }
}