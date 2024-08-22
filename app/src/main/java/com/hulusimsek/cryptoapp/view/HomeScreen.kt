package com.hulusimsek.cryptoapp.view

import PullToRefreshPage
import android.annotation.SuppressLint
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
import com.hulusimsek.cryptoapp.viewmodel.HomeViewModel


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState

import androidx.compose.runtime.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.hulusimsek.cryptoapp.model.CryptoItem
import com.hulusimsek.cryptoapp.ui.theme.BlueMunsell
import com.hulusimsek.cryptoapp.ui.theme.dusenKirmizi
import com.hulusimsek.cryptoapp.ui.theme.yukselenYesil
import com.hulusimsek.cryptoapp.util.Constants.getBtcSymbols
import com.hulusimsek.cryptoapp.viewmodel.MainViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalPagerApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val toastMessage by viewModel.toastMessage.collectAsState()
    val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()
    val filterCryptoList by viewModel.filterCryptoList.collectAsState()
    val cryptoList by viewModel.cryptoList.collectAsState()

    val tab0 by viewModel.tab0.collectAsState()
    val tab1 by viewModel.tab1.collectAsState()
    val tab2 by viewModel.tab2.collectAsState()
    val tab3 by viewModel.tab3.collectAsState()
    val tab4 by viewModel.tab4.collectAsState()

    val selectedSymbol by viewModel.selectedSymbol.collectAsState()
    val showDialog = remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(pageCount = { 5 }) // 5 sayfa için ayarlandı

    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val errorMessage by viewModel.errorMessage.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }

    LaunchedEffect(showDialog.value) {
        viewModel.selectTab(selectedTabIndex)
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged() // Aynı sayfaya tekrar geçiş yapılırsa gereksiz güncellemeyi engeller
            .collect { page ->
                viewModel.selectTab(page)
            }
    }

    var isSearchBarExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        PullToRefreshPage(
            isRefreshing = isLoading,
            onRefresh = { viewModel.refresh() }
        ) { contentModifier ->
            Column(modifier = contentModifier.fillMaxSize()) {

                if (!isSearchBarExpanded) {
                    Text(
                        text = "Crypto App",
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
                        .animateContentSize() // İçerik boyutunu animasyonla değiştir
                ) {
                    SearchBar(
                        onSearchQuery = { query ->
                            viewModel.searchCryptoList(query)
                        },
                        modifier = Modifier
                            .padding(horizontal = if (isSearchBarExpanded) 0.dp else 16.dp)
                            .fillMaxWidth(),
                        onActiveChange = { active ->
                            isSearchBarExpanded = active
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    edgePadding = 0.dp, // Kenar boşluklarını sıfırladık
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier
                                .tabIndicatorOffset(tabPositions[selectedTabIndex])
                                .padding(0.dp)
                        )
                    },
                    modifier = Modifier.padding(horizontal = 0.dp)
                ) {
                    Tab(
                        text = { Text(stringResource(R.string.newListings)) },
                        selected = selectedTabIndex == 0,
                        onClick = {
                            viewModel.selectTab(0) // Tab seçimi değiştiğinde sıralamayı güncelle
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        },
                        modifier = Modifier.padding(horizontal = 2.dp) // Tab'lar arasındaki boşluğu azaltma
                    )
                    Tab(
                        text = { Text(stringResource(R.string.gainers)) },
                        selected = selectedTabIndex == 1,
                        onClick = {
                            viewModel.selectTab(1) // Tab seçimi değiştiğinde sıralamayı güncelle
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        },
                        modifier = Modifier.padding(horizontal = 2.dp) // Tab'lar arasındaki boşluğu azaltma
                    )
                    Tab(
                        text = { Text(stringResource(R.string.losers)) },
                        selected = selectedTabIndex == 2,
                        onClick = {
                            viewModel.selectTab(2) // Tab seçimi değiştiğinde sıralamayı güncelle
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(2)
                            }
                        },
                        modifier = Modifier.padding(horizontal = 2.dp) // Tab'lar arasındaki boşluğu azaltma
                    )
                    Tab(
                        text = { Text(stringResource(R.string.marketCap)) },
                        selected = selectedTabIndex == 3,
                        onClick = {
                            viewModel.selectTab(3) // Tab seçimi değiştiğinde sıralamayı güncelle
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(3)
                            }
                        },
                        modifier = Modifier.padding(horizontal = 2.dp) // Tab'lar arasındaki boşluğu azaltma
                    )
                    Tab(
                        text = { Text(stringResource(R.string.volume24h)) },
                        selected = selectedTabIndex == 4,
                        onClick = {
                            viewModel.selectTab(4) // Tab seçimi değiştiğinde sıralamayı güncelle
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(4)
                            }
                        },
                        modifier = Modifier.padding(horizontal = 2.dp) // Tab'lar arasındaki boşluğu azaltma
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    key = { page -> page } // Pager'da her sayfanın anahtarını belirleyin
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
                        cryptoList = cryptoList,
                        filterCryptoList = tabCryptoList,
                        searchQuery = searchQuery,
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        selectedSymbol = selectedSymbol,
                        showDialog = showDialog,
                        viewModel = viewModel
                    )
                }

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (isLoading) {
                        CircularProgressIndicator(color = BlueMunsell)
                    }
                    if (errorMessage.isNotEmpty()) {
                        RetryView(error = errorMessage) {
                            viewModel.refresh()
                        }
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
                                    viewModel.selectSymbol(symbol)
                                    viewModel.selectTab(selectedTabIndex)
                                    viewModel.filterList()
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
@Composable
fun MarketSelectionDialog(
    showDialog: Boolean,
    btcSymbols: List<String>,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(text = "Piyasa Seçimi") },
            text = {
                LazyColumn {
                    items(btcSymbols) { symbol ->
                        Text(
                            text = symbol,
                            modifier = Modifier
                                .clickable {
                                    onSelect(symbol)
                                    onDismiss()
                                }
                                .padding(16.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { onDismiss() }) {
                    Text("Kapat")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onActiveChange: (Boolean) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
    onSearchQuery: (String) -> Unit = {}
) {
    var text by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(false) }
    val cryptoList by viewModel.cryptoList.collectAsState()
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
                viewModel.saveSearchQuery(it)
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
                                    viewModel.saveSearchQuery(item.query)
                                    onSearchQuery(item.query)
                                    isActive = false
                                }
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_close_24),
                            contentDescription = "delete icon",
                            modifier = Modifier.clickable {
                                viewModel.deleteSearchQuery(query = item)
                            }
                        )
                    }
                }
            }
        } else {
            LazyColumn {
                items(cryptoList) { item ->
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
                                    viewModel.saveSearchQuery(item.symbol)
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
fun CryptoListView(
    navController: NavController,
    filterCryptoList: List<CryptoItem>,
    searchQuery: String,
    cryptoList: List<CryptoItem>
) {
    LazyColumn(contentPadding = PaddingValues(5.dp)) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clickable { }
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Seçiniz",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 24.dp)
                    )
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.CenterVertically)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(2f)
                ) {
                    Text(
                        text = stringResource(R.string.price),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.padding(end = 16.dp),
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = stringResource(R.string.price_change_24h),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.padding(end = 48.dp),
                        textAlign = TextAlign.End
                    )
                }
            }
        }

        if (searchQuery.isEmpty()) {
            items(filterCryptoList) { crypto ->
                CryptoRow(navController = navController, crypto = crypto)
            }
        } else {
            items(cryptoList) { crypto ->
                CryptoRow(navController = navController, crypto = crypto)
            }
        }
    }
}

@Composable
fun CryptoList(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val cryptoList by viewModel.cryptoList.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()

    CryptoListView(cryptos = cryptoList, navController = navController)
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (isLoading) {
            CircularProgressIndicator(color = BlueMunsell)
        }
        if (errorMessage.isNotEmpty()) {
            RetryView(error = errorMessage) {
                viewModel.loadCrpyots()
            }
        }
    }
}

@Composable
fun CryptoListView(cryptos: List<CryptoItem>, navController: NavController) {
    LazyColumn(contentPadding = PaddingValues(10.dp)) {
        items(cryptos) { crypto ->
            CryptoRow(navController = navController, crypto = crypto)
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

@Composable
fun RetryView(
    error: String,
    onRetry: () -> Unit
) {
    Column {
        Text(text = error, color = Color.Red, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = { onRetry }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(text = "Retry")
        }
    }
}