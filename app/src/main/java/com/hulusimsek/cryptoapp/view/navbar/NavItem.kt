package com.hulusimsek.cryptoapp.view.navbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.res.painterResource
import com.hulusimsek.cryptoapp.R

sealed class NavItem(val path: String, val title: String, val iconId: Int) {
    object Home : NavItem(NavPath.HOME.toString(), NavTitle.HOME, R.drawable.baseline_home_24)
    object Markets : NavItem(NavPath.MARKETS.toString(), NavTitle.MARKETS, R.drawable.baseline_bar_chart_24)
    object List : NavItem(NavPath.LIST.toString(), NavTitle.LIST, R.drawable.baseline_currency_bitcoin_24)
    object Profile : NavItem(NavPath.PROFILE.toString(), NavTitle.PROFILE, R.drawable.baseline_home_24)
}
