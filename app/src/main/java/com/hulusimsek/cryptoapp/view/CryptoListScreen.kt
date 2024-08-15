package com.hulusimsek.cryptoapp.view

import android.telecom.StatusHints
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.hulusimsek.cryptoapp.R
import com.hulusimsek.cryptoapp.viewmodel.CryptoListViewModel


import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.runtime.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.res.colorResource
import androidx.work.ListenableWorker.Result.Retry
import com.hulusimsek.cryptoapp.entity.SearchQuery
import com.hulusimsek.cryptoapp.model.CryptoListItem
import com.hulusimsek.cryptoapp.ui.theme.Alabaster
import com.hulusimsek.cryptoapp.ui.theme.BlueMunsell
import com.hulusimsek.cryptoapp.ui.theme.Bone

@Composable
fun CryptoListScreen(
    navController: NavController,
    viewModel: CryptoListViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    var isSearchBarExpanded by remember { mutableStateOf(false) }
    val cryptoList by remember { viewModel.cryptoList }
    val isLoading by remember { viewModel.isLoading }
    val errorMessage by remember { viewModel.errorMessage }

    Surface(color = MaterialTheme.colorScheme.secondary, modifier = modifier.fillMaxSize()) {
        Column {
            if (!isSearchBarExpanded) {
                Text(
                    text = "Crypto App",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = BlueMunsell
                )
                Spacer(modifier = Modifier.height(10.dp))
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
                        .padding(horizontal = if (isSearchBarExpanded) 0.dp else 16.dp) // Padding ayarı
                        .fillMaxWidth(),
                    onActiveChange = { active ->
                        isSearchBarExpanded = active
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))


            // Listeyi yalnızca SearchBar genişletilmediğinde göster

                LazyColumn(contentPadding = PaddingValues(5.dp)) {
                    items(cryptoList) { crypto ->
                        CryptoRow(navController = navController, crypto = crypto)
                    }
                }

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (isLoading) {
                        CircularProgressIndicator(color = colorResource(id = R.color.color1))
                    }
                    if (errorMessage.isNotEmpty()) {
                        RetryView(error = errorMessage) {
                            viewModel.loadCrpyots()
                        }
                    }
                }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onActiveChange: (Boolean) -> Unit = {},
    viewModel: CryptoListViewModel = hiltViewModel(),
    onSearchQuery: (String) -> Unit = {}
    ) {
    var text by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(false) }
    val cryptoList by remember { viewModel.cryptoList }

    androidx.compose.material3.SearchBar(
        modifier = modifier,
        query = text,
        onQueryChange = {
            text = it
            onSearchQuery(it) // Her harfe basıldığında arama yap
        },
        onSearch = {
            if(it.isNotEmpty()) {
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
        if(text.isEmpty()) {
            LazyColumn {
                items(viewModel.searchQueryList.value.reversed()) { item ->
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
        }
        else {
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
fun CryptoList(navController: NavController, viewModel: CryptoListViewModel = hiltViewModel()){
    val cryptoList by remember {
        viewModel.cryptoList
    }
    val errorMessage by remember {
        viewModel.errorMessage
    }
    val isLoading by remember {
        viewModel.isLoading
    }
    CryptoListView(cryptos = cryptoList, navController = navController)
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if(isLoading){
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
fun CryptoListView(cryptos: List<CryptoListItem>, navController: NavController) {
    LazyColumn(contentPadding = PaddingValues(10.dp)) {
        items(cryptos) { crypto ->
            CryptoRow(navController = navController, crypto = crypto)
        }
    }
}

@Composable
fun CryptoRow(navController: NavController, crypto: CryptoListItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable {
                navController.navigate("crypto_detail_screen/${crypto.symbol}/${crypto.price}")
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
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = crypto.symbol,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = crypto.price,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
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
        Button(onClick = {onRetry}, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(text = "Retry")
        }
    }
}