package com.hulusimsek.cryptoapp.view

import CryptoDetailScreen
import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hulusimsek.cryptoapp.view.navbar.NavItem
import com.hulusimsek.cryptoapp.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerWithNavHost(
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()// ViewModel'inizi burada tanımlayın
) {
    val pagerState = rememberPagerState(pageCount = {4})
    val coroutineScope = rememberCoroutineScope() // CoroutineScope oluşturuluyor
    val navItems = listOf(NavItem.Home, NavItem.Markets, NavItem.List, NavItem.Profile)

    Scaffold(
        bottomBar = {
            val currentPage = pagerState.currentPage

            NavigationBar {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        alwaysShowLabel = true,
                        icon = { Icon(painter = painterResource(id = item.iconId), contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentPage == index,
                        onClick = {
                            viewModel.goToPage(index)
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                                // Ana sayfayı göstermek için mevcut sayfa yığını temizlenir
                                if (index == 0) { // Ana sayfaya gidiyorsak
                                    navController.popBackStack(navController.graph.startDestinationId, inclusive = false)
                                }
                            }

                        }


                    )
                }
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
                        HomeScreen(navController = navController)
                    }
                    composable("crypto_detail_screen/{cryptoId}") {
                        val cryptoId = it.arguments?.getString("cryptoId") ?: ""
                        CryptoDetailScreen(id = cryptoId, navController = navController)
                    }
                }

                1 -> NavHost(
                    navController = navController,
                    startDestination = "crypto_list_screen"
                ) {
                    composable("crypto_list_screen") {
                        HomeScreen(navController = navController)
                    }
                    composable("crypto_detail_screen/{cryptoId}") {
                        val cryptoId = it.arguments?.getString("cryptoId") ?: ""
                        CryptoDetailScreen(id = cryptoId, navController = navController)
                    }
                }

                // Eğer diğer sayfalar varsa onları da ekleyebilirsiniz
                // 2 -> NavHost(
                //     navController = navController,
                //     startDestination = "yet_another_screen"
                // ) {
                //     composable("yet_another_screen") {
                //         YetAnotherScreen()
                //     }
                // }
            }
        }
    }

}