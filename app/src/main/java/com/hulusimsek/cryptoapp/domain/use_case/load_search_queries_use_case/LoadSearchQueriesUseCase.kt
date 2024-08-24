package com.hulusimsek.cryptoapp.domain.use_case.load_search_queries_use_case

import com.hulusimsek.cryptoapp.data.database.entity.SearchQuery
import com.hulusimsek.cryptoapp.domain.repository.CryptoRepository
import javax.inject.Inject

class LoadSearchQueriesUseCase @Inject constructor(
    private val repository: CryptoRepository
) {
    suspend operator fun invoke(): List<SearchQuery> {
        return repository.getSearchQuery()
    }
}