package com.hulusimsek.cryptoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.hulusimsek.cryptoapp.ui.theme.CryptoAppTheme
import com.hulusimsek.cryptoapp.view.PagerWithNavHost
import com.hulusimsek.cryptoapp.view.SplashScreen
import com.hulusimsek.cryptoapp.viewmodel.HomeViewModel
import com.hulusimsek.cryptoapp.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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
