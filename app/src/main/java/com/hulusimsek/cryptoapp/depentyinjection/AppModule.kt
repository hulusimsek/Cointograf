package com.hulusimsek.cryptoapp.depentyinjection

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.hulusimsek.cryptoapp.repository.CryptoRepository
import com.hulusimsek.cryptoapp.repository.CryptoRepositoryInterface
import com.hulusimsek.cryptoapp.roomDB.CryptoDao
import com.hulusimsek.cryptoapp.roomDB.CryptoDatabase
import com.hulusimsek.cryptoapp.service.CryptoAPI
import com.hulusimsek.cryptoapp.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun injectRoomDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, CryptoDatabase::class.java, "CryptoDB").build()

    @Singleton
    @Provides
    fun injectDao(database: CryptoDatabase) = database.artDao()


    @Singleton
    @Provides
    fun provideCryptoApi() : CryptoAPI {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(CryptoAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideCryptoRepository(api: CryptoAPI, dao: CryptoDao) = CryptoRepository(api, dao) as CryptoRepositoryInterface

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }
}