package com.hulusimsek.cryptoapp.view

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hulusimsek.cryptoapp.R
import com.hulusimsek.cryptoapp.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToHome: () -> Unit, viewModel: HomeViewModel) {
    val isLoading by viewModel.isLoading.collectAsState()

    // Animasyon için gerekli değerler
    val logoOffset by animateDpAsState(
        targetValue = if (isLoading) 0.dp else 1000.dp,
        animationSpec = tween(durationMillis = 1000)
    )
    val logoScale by animateFloatAsState(
        targetValue = if (isLoading) 1f else 2f,
        animationSpec = tween(durationMillis = 1000)
    )

    // Verileri yükleme ve animasyonun başlaması
    LaunchedEffect(Unit) {
        viewModel.loadCryptos()
        viewModel.loadSearchQueries() // Verileri yüklemeye başla
        delay(2000)
        onNavigateToHome()
        // Veriler yüklenene kadar bekle
    }

    // SplashScreen animasyonu
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Logo animasyonu
        Image(
            painter = painterResource(R.drawable.baseline_home_24),
            contentDescription = null,
            modifier = Modifier
                .offset(y = logoOffset)
                .scale(logoScale)
                .animateContentSize() // İçerik boyutunu animasyonla değiştir
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}



