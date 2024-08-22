package com.hulusimsek.cryptoapp.view

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hulusimsek.cryptoapp.R
import com.hulusimsek.cryptoapp.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun PagerWithTabs(
    viewModel: HomeViewModel, // ViewModel'inizi burada tanımlayın
    pagerState: PagerState,
    content: @Composable (Modifier) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val selectedTabIndex = remember { pagerState.currentPage }

    val contentModifier = Modifier
        .fillMaxWidth()

    Scaffold(
        topBar = {
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
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            content(contentModifier)
        }
    }
}

