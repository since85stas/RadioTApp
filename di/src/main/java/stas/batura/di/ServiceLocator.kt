package stas.batura.di

import android.content.Context
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.util.Util
import okhttp3.OkHttpClient
import ru.batura.stat.batchat.repository.room.PodcastDao
import stas.batura.protostore.Preference
import stas.batura.protostore.PreferenceImp
import stas.batura.protostore.UserPreferencesSerializer
import stas.batura.radioproject.data.Repository
import stas.batura.retrofit.IRetrofit
import stas.batura.retrofit.RetrofitClient
import stas.batura.room.RadioDatabase

object ServiceLocator {

    private var dao: PodcastDao? = null

    private var retrofit: IRetrofit? = null

    private var preferecen: Preference? = null

    private var repository: Repository? = null

    private fun provideDao(application: Context): PodcastDao {
        if (dao != null) return dao!!
        else {
            dao = RadioDatabase.getInstance(application).radioDatabaseDao
            return dao!!
        }
    }

    private fun provideRetrofit(): IRetrofit {
        if (retrofit != null) return retrofit!!
        else {
            retrofit = RetrofitClient.netApi.servise!!
            return retrofit!!
        }
    }

    private fun providePref(application: Context): Preference {
        if (preferecen != null) return preferecen!!
        else {
            preferecen =   PreferenceImp(application)
            return preferecen!!
        }
    }

    fun provideRepository(context: Context): Repository {
        if (repository != null) return repository!!
        else {
            return Repository(
                provideDao(context),
                provideRetrofit(),
                providePref(context)
            )
        }
    }



}