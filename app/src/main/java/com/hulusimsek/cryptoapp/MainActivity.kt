package com.hulusimsek.cryptoapp

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
import com.hulusimsek.cryptoapp.view.AppNavGraph
import com.hulusimsek.cryptoapp.view.CryptoListScreen
import com.hulusimsek.cryptoapp.view.PagerWithNavHost
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
                val mainViewModel: MainViewModel = viewModel()
                val navController = rememberNavController()


                Scaffold(
                    bottomBar = {
                        PagerWithNavHost(navController = navController)
                    }
                ) { innerPadding ->
                }

            }
        }
    }
}
