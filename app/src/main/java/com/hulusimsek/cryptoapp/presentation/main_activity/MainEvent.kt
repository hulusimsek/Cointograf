package com.hulusimsek.cryptoapp.presentation.main_activity

sealed class MainEvent {
    data class GoToPage(val page: Int) : MainEvent()
    object NextPage : MainEvent()
    object PreviousPage : MainEvent()
}