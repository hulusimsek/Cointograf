package com.hulusimsek.cryptoapp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_queries")
data class SearchQuery(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val query: String
)