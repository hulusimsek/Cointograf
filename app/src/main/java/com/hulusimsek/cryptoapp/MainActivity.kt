package com.hulusimsek.cryptoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.hulusimsek.cryptoapp.ui.theme.CryptoAppTheme
import com.hulusimsek.cryptoapp.view.PagerWithNavHost
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


                        PagerWithNavHost(navController = navController)



            }
        }
    }
}
