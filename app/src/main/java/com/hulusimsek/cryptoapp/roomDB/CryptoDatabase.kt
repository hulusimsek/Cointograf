package com.hulusimsek.cryptoapp.roomDB

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hulusimsek.cryptoapp.entity.SearchQuery

@Database(entities = [SearchQuery::class], version = 1)
abstract  class CryptoDatabase: RoomDatabase() {
    abstract fun artDao() : CryptoDao
}