package com.hulusimsek.cryptoapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hulusimsek.cryptoapp.data.database.dao.CryptoDao
import com.hulusimsek.cryptoapp.data.database.entity.SearchQuery

@Database(entities = [SearchQuery::class], version = 1)
abstract  class CryptoDatabase: RoomDatabase() {
    abstract fun artDao() : CryptoDao
}