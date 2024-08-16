import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.BottomNavigationItem
import com.hulusimsek.cryptoapp.R
import com.hulusimsek.cryptoapp.view.CryptoListScreen
import com.hulusimsek.cryptoapp.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomNavSwipeScreen(viewModel: MainViewModel = viewModel(), navController: NavController) {
    val scope = rememberCoroutineScope()
    val pageState = rememberPagerState(pageCount = { viewModel.pageCount.value })

    // Sayfa sayısını ayarla
    LaunchedEffect(Unit) {
        viewModel.setPageCount(5) // Örnek: sayfa sayısını ayarla
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(124.dp)
                    .navigationBarsPadding(),
                content = {
                    val selectedTabIndex by remember { derivedStateOf { pageState.currentPage } }

                    NavigationBarItem(
                        icon = {
                            Icon(
                                painterResource(id = R.drawable.baseline_home_24),
                                contentDescription = "Page 0",
                                modifier = Modifier.size(if (selectedTabIndex == 0) 30.dp else 24.dp).padding(0.dp)// Seçili ikonu büyüt
                            )
                        },
                        label = { Text("Page 0") },
                        selected = pageState.currentPage == 0,
                        onClick = {
                            scope.launch {
                                pageState.animateScrollToPage(0)
                                viewModel.goToPage(0)
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Magenta,
                            unselectedIconColor = Color.LightGray,
                            selectedTextColor = Color.Magenta,
                            unselectedTextColor = Color.LightGray,
                            indicatorColor = Color.Transparent,


                            )
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painterResource(id = R.drawable.baseline_home_24),
                                contentDescription = "Page 1",
                                modifier = Modifier.size(if (selectedTabIndex == 1) 30.dp else 24.dp) // Seçili ikonu büyüt

                            )
                        },
                        label = { Text("Page 1") },
                        selected = pageState.currentPage == 1,
                        onClick = {
                            scope.launch {
                                pageState.animateScrollToPage(1)
                                viewModel.goToPage(1)
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Magenta,
                            unselectedIconColor = Color.LightGray,
                            selectedTextColor = Color.Magenta,
                            unselectedTextColor = Color.LightGray,
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painterResource(id = R.drawable.baseline_home_24),
                                contentDescription = "Page 1",
                                modifier = Modifier.size(if (selectedTabIndex == 2) 30.dp else 24.dp) // Seçili ikonu büyüt
                            )
                        },
                        label = { Text("Page 2") },
                        selected = pageState.currentPage == 2,
                        onClick = {
                            scope.launch {
                                pageState.animateScrollToPage(2)
                                viewModel.goToPage(2)
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Magenta,
                            unselectedIconColor = Color.LightGray,
                            selectedTextColor = Color.Magenta,
                            unselectedTextColor = Color.LightGray,
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painterResource(id = R.drawable.baseline_home_24),
                                contentDescription = "Page 1",
                                modifier = Modifier.size(if (selectedTabIndex == 3) 30.dp else 24.dp) // Seçili ikonu büyüt
                            )
                        },
                        label = { Text("Page 3") },
                        selected = pageState.currentPage == 3,
                        onClick = {
                            scope.launch {
                                pageState.animateScrollToPage(3)
                                viewModel.goToPage(3)
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Magenta,
                            unselectedIconColor = Color.LightGray,
                            selectedTextColor = Color.Magenta,
                            unselectedTextColor = Color.LightGray,
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painterResource(id = R.drawable.baseline_home_24),
                                contentDescription = "Page 1",
                                modifier = Modifier.size(if (selectedTabIndex == 4) 30.dp else 24.dp) // Seçili ikonu büyüt

                            )
                        },
                        label = { Text("Page 4") },
                        selected = pageState.currentPage == 4,
                        onClick = {
                            scope.launch {
                                pageState.animateScrollToPage(4)
                                viewModel.goToPage(4)
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Magenta,
                            unselectedIconColor = Color.LightGray,
                            selectedTextColor = Color.Magenta,
                            unselectedTextColor = Color.LightGray,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            )
        }
    ) { innerPadding ->
        // Sayfalar
        HorizontalPager(
            state = pageState,
            modifier = Modifier.padding(innerPadding)
        ) { page ->
            when (page) {
                0 -> {
                    // Sayfa 0 içeriği
                    CryptoListScreen(navController = navController)
                }

                1 -> {
                    // Sayfa 1 içeriği
                    Image(
                        painterResource(id = R.drawable.baseline_home_24),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                2 -> {
                    // Sayfa 2 içeriği
                    Image(
                        painterResource(id = R.drawable.baseline_home_24),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                3 -> {
                    // Sayfa 3 içeriği
                    Image(
                        painterResource(id = R.drawable.baseline_home_24),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                4 -> {
                    // Sayfa 4 içeriği
                    Image(
                        painterResource(id = R.drawable.baseline_home_24),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

