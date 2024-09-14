package com.hulusimsek.cryptoapp.data.depentyinjection

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.hulusimsek.cryptoapp.data.repository.CryptoRepositoryImp
import com.hulusimsek.cryptoapp.domain.repository.CryptoRepository
import com.hulusimsek.cryptoapp.data.database.dao.CryptoDao
import com.hulusimsek.cryptoapp.data.database.CryptoDatabase
import com.hulusimsek.cryptoapp.data.remote.CryptoAPI
import com.hulusimsek.cryptoapp.domain.use_case.delete_search_query_use_case.DeleteSearchQueryUseCase
import com.hulusimsek.cryptoapp.domain.use_case.get_crypto_details_use_case.GetCryptoDetailsUseCase
import com.hulusimsek.cryptoapp.domain.use_case.get_cryptos.GetCryptosUseCase
import com.hulusimsek.cryptoapp.domain.use_case.load_search_queries_use_case.LoadSearchQueriesUseCase
import com.hulusimsek.cryptoapp.domain.use_case.save_search_query_use_case.SaveSearchQueryUseCase
import com.hulusimsek.cryptoapp.data.WebSocketClient // Paket adını doğru yazdığınızdan emin olun

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
    fun provideCryptoRepository(api: CryptoAPI, dao: CryptoDao) = CryptoRepositoryImp(api, dao) as CryptoRepository

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    fun provideGetCryptosUseCase(
        repository: CryptoRepository,
        context: Context
    ): GetCryptosUseCase {
        return GetCryptosUseCase(repository, context)
    }

    @Provides
    fun provideLoadSearchQueriesUseCase(
        repository: CryptoRepository
    ): LoadSearchQueriesUseCase {
        return LoadSearchQueriesUseCase(repository)
    }

    @Provides
    fun provideSaveSearchQueryUseCase(
        repository: CryptoRepository
    ): SaveSearchQueryUseCase {
        return SaveSearchQueryUseCase(repository)
    }

    @Provides
    fun provideDeleteSearchQueryUseCase(
        repository: CryptoRepository
    ): DeleteSearchQueryUseCase {
        return DeleteSearchQueryUseCase(repository)
    }

    @Provides
    fun provideGetCryptoDetailUseCase(
        repository: CryptoRepository,
        context: Context
    ): GetCryptoDetailsUseCase {
        return GetCryptoDetailsUseCase(repository,context)
    }

    @Provides
    @Singleton
    fun provideWebSocketClient(): WebSocketClient {
        return WebSocketClient()
    }
}