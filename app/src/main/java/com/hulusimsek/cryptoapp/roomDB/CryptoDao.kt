package com.hulusimsek.cryptoapp.roomDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.hulusimsek.cryptoapp.entity.SearchQuery

@Dao
interface CryptoDao {
    @Insert
    suspend fun insertQuery(searchQuery: SearchQuery)

    @Delete
    suspend fun deleteQuery(searchQuery: SearchQuery)

    @Query("SELECT * FROM search_queries")
    suspend fun getAllQueries(): List<SearchQuery>

    @Query("DELETE FROM search_queries WHERE query = :query")
    suspend fun deleteSearchQueryByQuery(query: String)
}