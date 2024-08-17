package com.hulusimsek.cryptoapp.view

import CryptoDetailScreen
import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hulusimsek.cryptoapp.R
import com.hulusimsek.cryptoapp.viewmodel.CryptoListViewModel
import com.hulusimsek.cryptoapp.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerWithNavHost(
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()// ViewModel'inizi burada tanımlayın
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 3 }) // Sayfa sayısı

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primary,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                // Sayfa 0 için BottomNavigationItem
                BottomNavigationItem(
                    icon = { Icon(Icons.Filled.Home, "Page 0") },
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    selectedContentColor = Color.Magenta,
                    unselectedContentColor = Color.LightGray,
                    label = { Text(text = "Page 0") }
                )

                // Sayfa 1 için BottomNavigationItem
                BottomNavigationItem(
                    icon = { Icon(Icons.Filled.Search, "Page 1") },
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    selectedContentColor = Color.Magenta,
                    unselectedContentColor = Color.LightGray,
                    label = { Text(text = "Page 1") }
                )

                // Sayfa 2 için BottomNavigationItem
                BottomNavigationItem(
                    icon = { Icon(Icons.Filled.Favorite, "Page 2") },
                    selected = pagerState.currentPage == 2,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(2)
                        }
                    },
                    selectedContentColor = Color.Magenta,
                    unselectedContentColor = Color.LightGray,
                    label = { Text(text = "Page 2") }
                )
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { page ->
            when (page) {
                0 -> NavHost(
                    navController = navController,
                    startDestination = "crypto_list_screen"
                ) {
                    composable("crypto_list_screen") {
                        CryptoListScreen(navController = navController)
                    }
                    composable("crypto_detail_screen/{cryptoId}") { backStackEntry ->
                        val cryptoId = backStackEntry.arguments?.getString("cryptoId") ?: ""
                        CryptoDetailScreen(id = cryptoId, navController = navController)
                    }
                }
                1 -> NavHost(
                    navController = navController,
                    startDestination = "crypto_list_screen"
                ) {
                    composable("crypto_list_screen") {
                        CryptoListScreen(navController = navController)
                    }
                    composable("crypto_detail_screen/{cryptoId}") { backStackEntry ->
                        val cryptoId = backStackEntry.arguments?.getString("cryptoId") ?: ""
                        CryptoDetailScreen(id = cryptoId, navController = navController)
                    }
                }
            }
        }
    }
}
@Composable
fun CryptoListScreenContent(navController: NavHostController) {
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