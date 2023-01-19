package stas.batura.di

import android.content.Context
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import kotlinx.coroutines.flow.Flow
import stas.batura.analitics.AnaliticManager
import stas.batura.analitics.AnaliticManagerImpl
import stas.batura.room.RadioDao
import stas.batura.protostore.Preference
import stas.batura.protostore.PreferenceImp
import stas.batura.repository.PodcastRepository
import stas.batura.retrofit.IPodcasts
import stas.batura.retrofit.RetrofitClient
import stas.batura.room.RadioDatabase
import stas.batura.room.podcast.Podcast
import stas.batura.timer.TimerImpl
import java.io.File
import java.util.*

object ServiceLocator {


    private val ONLINE_PODCAST = Podcast(
        podcastId = -1,
        title = "Эфир",
        audioUrl = "https://stream.radio-t.com/"
    )

    private var dao: RadioDao? = null

    private var retrofit: IPodcasts? = null

    private var preferecen: Preference? = null

    private var repository: PodcastRepository? = null

    private var application: Context? = null

    private var  cache :SimpleCache? = null

    private var analictic: AnaliticManagerImpl? = null

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

    private fun provideRetrofit(): IPodcasts {
        if (retrofit != null) return retrofit!!
        else {
            retrofit = RetrofitClient.netApi.podcasts!!
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

    fun provideRepository(): PodcastRepository {
        if (repository != null) return repository!!
        else {
            return PodcastRepository(
                provideDao(),
                provideRetrofit(),
                providePref(),
                provideOnlinePodcastLink()
            )
        }
    }

    fun provideAnalitic(): AnaliticManager {
        if (analictic != null) return analictic!!
        else {
            return AnaliticManagerImpl(provideContext())
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

    fun provideOnlinePodcastLink(): Podcast {
        return ONLINE_PODCAST
    }

    fun provideTimerValues(): Flow<Long> {
        val goalTime = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"))
        goalTime.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY )
        goalTime.set(Calendar.HOUR_OF_DAY, 23)
        goalTime.set(Calendar.MINUTE, 0)
        goalTime.set(Calendar.SECOND, 0)
        goalTime.set(Calendar.MILLISECOND, 0)

        val timer: stas.batura.timer.Timer = TimerImpl(goalTime)

        return timer.getValues()
    }

}