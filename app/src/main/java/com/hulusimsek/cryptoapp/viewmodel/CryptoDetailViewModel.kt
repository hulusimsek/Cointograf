package com.hulusimsek.cryptoapp.viewmodel

import androidx.lifecycle.ViewModel
import com.hulusimsek.cryptoapp.model.CryptoItem
import com.hulusimsek.cryptoapp.model.CryptoListItem
import com.hulusimsek.cryptoapp.repository.CryptoRepository
import com.hulusimsek.cryptoapp.repository.CryptoRepositoryInterface
import com.hulusimsek.cryptoapp.util.Resource
import javax.inject.Inject

class CryptoDetailViewModel @Inject constructor(
    private val repository: CryptoRepositoryInterface
) : ViewModel(){


    suspend fun getCrypto(id: String) : Resource<CryptoItem> {
        return repository.getCrypto(id)
    }
}