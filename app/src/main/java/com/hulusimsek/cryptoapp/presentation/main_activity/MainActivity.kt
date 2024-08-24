package com.hulusimsek.cryptoapp.presentation.main_activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.hulusimsek.cryptoapp.presentation.theme.CryptoAppTheme
import com.hulusimsek.cryptoapp.presentation.navbar.PagerWithNavHost
import com.hulusimsek.cryptoapp.presentation.splash_screen.views.SplashScreen
import com.hulusimsek.cryptoapp.presentation.home_screen.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptoAppTheme {
                val mainViewModel: MainViewModel = viewModel()
                val navController = rememberNavController()
                val homeViewModel: HomeViewModel = hiltViewModel()

                SplashScreen(
                    viewModel = homeViewModel,
                    onNavigateToHome = {
                        setContent {
                            CryptoAppTheme {
                                PagerWithNavHost(navController = navController,homeViewModel)
                            }
                        }
                    }
                )


            }
        }
    }
}
