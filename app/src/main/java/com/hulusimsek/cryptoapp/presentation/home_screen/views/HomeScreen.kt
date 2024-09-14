package com.hulusimsek.cryptoapp.presentation.home_screen.views

import PullToRefreshPage
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hulusimsek.cryptoapp.R
import com.hulusimsek.cryptoapp.presentation.home_screen.HomeViewModel


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState

import androidx.compose.runtime.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hulusimsek.cryptoapp.data.remote.dto.CryptoItem
import com.hulusimsek.cryptoapp.presentation.home_screen.CryptosEvent
import com.hulusimsek.cryptoapp.presentation.theme.BlueMunsell
import com.hulusimsek.cryptoapp.presentation.theme.dusenKirmizi
import com.hulusimsek.cryptoapp.presentation.theme.yukselenYesil
import com.hulusimsek.cryptoapp.util.Constants.getBtcSymbols
import com.hulusimsek.cryptoapp.presentation.main_activity.MainViewModel
import com.hulusimsek.cryptoapp.presentation.util.RetryView
import kotlinx.coroutines.launch



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedGetBackStackEntry")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val selectedTabIndex by viewModel.selectedTabIndex.collectAsStateWithLifecycle()

    val tab0 by viewModel.tab0.collectAsStateWithLifecycle()
    val tab1 by viewModel.tab1.collectAsStateWithLifecycle()
    val tab2 by viewModel.tab2.collectAsStateWithLifecycle()
    val tab3 by viewModel.tab3.collectAsStateWithLifecycle()
    val tab4 by viewModel.tab4.collectAsStateWithLifecycle()

    val selectedSymbol by viewModel.selectedSymbol.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    val showDialog = remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = {5})

    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    val lifecycleOwner = LocalLifecycleOwner.current

    val currentBackStackEntry by navController.currentBackStackEntryAsState()


    LaunchedEffect(pagerState.currentPage) {
        viewModel.onEvent(CryptosEvent.SelectTab(pagerState.currentPage))
    }

    LaunchedEffect(selectedTabIndex, searchResults.map { it.symbol }, state.isLoading) {
        if(searchResults.isNotEmpty()) {
            viewModel.updateSelectedSymbols(searchResults.map { it.symbol })
        }
        else {
            if (!state.isLoading) {
                val symbols = when (selectedTabIndex) {
                    0 -> tab0.map { it.symbol }
                    1 -> tab1.map { it.symbol }
                    2 -> tab2.map { it.symbol }
                    3 -> tab3.map { it.symbol }
                    4 -> tab4.map { it.symbol }
                    else -> emptyList()
                }

                if (symbols.isNotEmpty()) {
                    viewModel.updateSelectedSymbols(symbols)
                } else {
                    // Eğer semboller boşsa, WebSocket bağlantısını güncellemeye gerek yok
                    viewModel.webSocketClient.disconnect()
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val symbols = when (selectedTabIndex) {
            0 -> tab0.map { it.symbol }
            1 -> tab1.map { it.symbol }
            2 -> tab2.map { it.symbol }
            3 -> tab3.map { it.symbol }
            4 -> tab4.map { it.symbol }
            else -> emptyList()
        }

        val observer = LifecycleEventObserver { _, event ->
            // Eğer HomeScreen görünür durumdaysa WebSocket bağlantısını aç, değilse kapat
            if (currentBackStackEntry?.destination?.route == "home_screen_route") {
                when (event) {
                    Lifecycle.Event.ON_RESUME -> viewModel.updateSelectedSymbols(symbols)
                    Lifecycle.Event.ON_PAUSE -> viewModel.webSocketClient.disconnect()
                    Lifecycle.Event.ON_STOP -> viewModel.webSocketClient.disconnect()
                    else -> {}
                }
            } else {
                // Eğer HomeScreen görünmüyorsa WebSocket'i kapat
                viewModel.webSocketClient.disconnect()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onEvent(CryptosEvent.ClearToastMessage)        }
    }

    var isSearchBarExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        PullToRefreshPage(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.onEvent(CryptosEvent.Refresh) }
        ) { contentModifier ->
            Column(modifier = contentModifier.fillMaxSize()) {

                if (!isSearchBarExpanded) {
                    Text(
                        text = "Cointograf",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlueMunsell
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize() // Animate size changes smoothly
                ) {
                    SearchBar(
                        onSearchQuery = { query ->
                            viewModel.onEvent(CryptosEvent.SearchCrypto(query))
                        },
                        viewModel = viewModel,
                        modifier = Modifier
                            .padding(horizontal = if (isSearchBarExpanded) 0.dp else 16.dp)
                            .fillMaxWidth(),
                        onActiveChange = { active ->
                            isSearchBarExpanded = active
                        },
                        QueryText = searchQuery
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    edgePadding = 0.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage])
                        )
                    },
                    modifier = Modifier.padding(horizontal = 0.dp)
                ) {
                    listOf(
                        stringResource(R.string.newListings),
                        stringResource(R.string.gainers),
                        stringResource(R.string.losers),
                        stringResource(R.string.marketCap),
                        stringResource(R.string.volume24h)
                    ).forEachIndexed { index, title ->
                        Tab(
                            text = { Text(title) },
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                    viewModel.onEvent(CryptosEvent.SelectTab(index))
                                }
                            },
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val tabCryptoList = when (page) {
                        0 -> tab0
                        1 -> tab1
                        2 -> tab2
                        3 -> tab3
                        4 -> tab4
                        else -> emptyList()
                    }

                    HomeLazyColumn(
                        navController = navController,
                        cryptoList = searchResults,
                        filterCryptoList = tabCryptoList,
                        searchQuery = searchQuery,
                        isLoading = state.isLoading,
                        errorMessage = state.error,
                        selectedSymbol = selectedSymbol,
                        showDialog = showDialog,
                        viewModel = viewModel
                    )
                }

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (state.isLoading) {
                        CircularProgressIndicator(color = BlueMunsell)
                    }
                    if (state.error.isNotEmpty()) {
                        RetryView(error = state.error) {
                            viewModel.onEvent(CryptosEvent.Refresh)                        }
                    }
                }
            }
        }

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text(text = stringResource(R.string.select)) },
                text = {
                    Column {
                        getBtcSymbols(context).forEach { symbol ->
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.onEvent(CryptosEvent.SelectSymbol(symbol))
                                    viewModel.onEvent(CryptosEvent.SelectTab(pagerState.currentPage))
                                    viewModel.onEvent(CryptosEvent.FilterList)
                                    showDialog.value = false
                                }) {
                                Text(
                                    text = symbol,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showDialog.value = false }
                    ) {
                        Text(stringResource(R.string.ok))
                    }
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onActiveChange: (Boolean) -> Unit = {},
    viewModel: HomeViewModel,
    onSearchQuery: (String) -> Unit = {},
    QueryText: String
) {
    var text by remember { mutableStateOf(QueryText) }
    var isActive by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()
    val searchQueryList by viewModel.searchQueryList.collectAsState()

    androidx.compose.material3.SearchBar(
        modifier = modifier,
        query = text,
        onQueryChange = {
            text = it
            onSearchQuery(it) // Her harfe basıldığında arama yap
        },
        onSearch = {
            if (it.isNotEmpty()) {
                onSearchQuery(text) // Search fonksiyonunu çağır
                viewModel.onEvent(CryptosEvent.SaveSearchQuery(it))
                isActive = false
            }
        },
        active = isActive,
        onActiveChange = {
            isActive = it
            onActiveChange(it) // Aktivasyon durumunu güncelle
        },
        placeholder = { Text(text = "Search") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon"
            )
        },
        trailingIcon = {
            if (isActive) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Icon",
                    modifier = Modifier.clickable {
                        if (text.isNotEmpty()) {
                            text = ""
                            onSearchQuery("")
                        } else {
                            isActive = false
                            // Close butonuna tıklanınca odaklanmayı kapat
                            onActiveChange(false)
                        }
                    }
                )
            }
        }
    ) {
        if (text.isEmpty()) {
            LazyColumn {
                items(searchQueryList.reversed()) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically, // Yatay hizalamayı sağlar
                        horizontalArrangement = Arrangement.SpaceBetween // Öğeler arasındaki boşluğu ayarlar
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_history_24),
                            contentDescription = "history icon",
                            modifier = Modifier.padding(end = 14.dp)
                        )
                        Text(
                            text = item.query,
                            modifier = Modifier
                                .weight(1f) // Yüksekliğin aynı hizada kalmasını sağlar
                                .padding(end = 14.dp)
                                .clickable {
                                    text = item.query // Text alanını güncelle
                                    viewModel.onEvent(CryptosEvent.SaveSearchQuery(item.query))

                                    onSearchQuery(item.query)
                                    isActive = false
                                }
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_close_24),
                            contentDescription = "delete icon",
                            modifier = Modifier.clickable {
                                viewModel.onEvent(CryptosEvent.DeleteSearchQuery(query = item))

                            }
                        )
                    }
                }
            }
        } else {
            LazyColumn {
                items(state.coins) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically, // Yatay hizalamayı sağlar
                        horizontalArrangement = Arrangement.SpaceBetween // Öğeler arasındaki boşluğu ayarlar
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "history icon",
                            modifier = Modifier.padding(end = 14.dp)
                        )
                        Text(
                            text = item.symbol,
                            modifier = Modifier
                                .weight(1f) // Yüksekliğin aynı hizada kalmasını sağlar
                                .padding(end = 14.dp)
                                .clickable {
                                    text = item.symbol // Text alanını güncelle
                                    viewModel.onEvent(CryptosEvent.SaveSearchQuery(item.symbol))
                                    onSearchQuery(item.symbol)
                                    isActive = false
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CryptoRow(
    navController: NavController, mainViewModel: MainViewModel = hiltViewModel(),
    crypto: CryptoItem
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable {
                navController.navigate("crypto_detail_screen/${crypto.symbol}")
            },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // İsim ve Surname
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (crypto.name != null && crypto.surname != null) {
                    Text(
                        text = crypto.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "/${crypto.surname}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                } else {
                    Text(
                        text = crypto.symbol,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

            }

            // Fiyat ve Yüzdelik Değişim
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = crypto.lastPrice,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp)) // Fiyat ile yüzdelik değişim arasına boşluk ekleyin

                // Yüzdelik değişim kutusu
                Box(
                    modifier = Modifier
                        .background(
                            color = if (crypto.priceChangePercent.startsWith("-")) dusenKirmizi else yukselenYesil,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp) // Padding ekleyin
                ) {
                    Text(
                        text = crypto.priceChangePercent + "%",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 16.sp), // Font boyutunu artırın
                        color = Color.White
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Navigate to details",
                tint = MaterialTheme.colorScheme.primary
            )
        }

    }
}