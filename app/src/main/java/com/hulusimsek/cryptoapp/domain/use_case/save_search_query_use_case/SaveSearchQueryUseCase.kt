package com.hulusimsek.cryptoapp.domain.use_case.save_search_query_use_case

import com.hulusimsek.cryptoapp.data.database.entity.SearchQuery
import com.hulusimsek.cryptoapp.domain.repository.CryptoRepository
import javax.inject.Inject

class SaveSearchQueryUseCase @Inject constructor(
    private val repository:CryptoRepository
) {
    suspend operator fun invoke(query: String) {
        repository.deleteSearchQueryByQuery(query)
        repository.insertSearchQuery(SearchQuery(query = query))
    }
}