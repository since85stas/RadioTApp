package stas.batura.di

import android.content.Context
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import stas.batura.room.RadioDao
import stas.batura.protostore.Preference
import stas.batura.protostore.PreferenceImp
import stas.batura.radioproject.data.Repository
import stas.batura.retrofit.IRetrofit
import stas.batura.retrofit.RetrofitClient
import stas.batura.room.RadioDatabase
import java.io.File

object ServiceLocator {

    private var dao: RadioDao? = null

    private var retrofit: IRetrofit? = null

    private var preferecen: Preference? = null

    private var repository: Repository? = null

    private var application: Context? = null

    private var  cache :SimpleCache? = null

    fun setContext(context: Context) {
        application = context
    }

    fun provideContext(): Context {
        if (application != null) {
            return application!!
        } else {
            throw IllegalAccessException()
        }
    }

    private fun provideDao(): RadioDao {
        if (dao != null) return dao!!
        else {
            dao = RadioDatabase.getInstance(application!!).radioDatabaseDao
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

    private fun providePref(): Preference {
        if (preferecen != null) return preferecen!!
        else {
            preferecen =   PreferenceImp(application!!)
            return preferecen!!
        }
    }

    fun provideRepository(): Repository {
        if (repository != null) return repository!!
        else {
            return Repository(
                provideDao(),
                provideRetrofit(),
                providePref()
            )
        }
    }

    fun providePodcastAudioCacheDir(): String {
        val dir = provideContext().cacheDir.absolutePath + "/podcast/"
        val file = File(dir)
        file.mkdir()
        return dir
    }

    fun provideExoCache(): SimpleCache {

        if (cache != null) {
            return cache!!
        } else {
            cache =
                SimpleCache(
                    File(provideContext().cacheDir.absolutePath + "/exoplayer"),
                    LeastRecentlyUsedCacheEvictor(1024 * 1024 * 100)
                ) // 100 Mb max

            return cache!!
        }
    }



}