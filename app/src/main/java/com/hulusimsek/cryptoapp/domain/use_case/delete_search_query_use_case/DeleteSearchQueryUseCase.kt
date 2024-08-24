package com.hulusimsek.cryptoapp.domain.use_case.delete_search_query_use_case

import com.hulusimsek.cryptoapp.data.database.entity.SearchQuery
import com.hulusimsek.cryptoapp.domain.repository.CryptoRepository
import javax.inject.Inject

class DeleteSearchQueryUseCase @Inject constructor(
    private val repository: CryptoRepository
) {
    suspend operator fun invoke(query: SearchQuery) {
        repository.deleteSearchQuery(query)
    }
}