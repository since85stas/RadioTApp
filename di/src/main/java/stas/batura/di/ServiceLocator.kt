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
import stas.batura.repository.NewsRepository
import stas.batura.repository.PodcastRepository
import stas.batura.retrofit.INews
import stas.batura.retrofit.IPodcasts
import stas.batura.retrofit.RetrofitClient
import stas.batura.room.RadioDatabase
import stas.batura.data.Podcast
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

//    private var retrofit: RetrofitClient? = null

    private var preferecen: Preference? = null

    private var podcastRepository: PodcastRepository? = null

    private var newsRepository: NewsRepository? = null

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

    private fun providePodcastApi(): IPodcasts {
            return RetrofitClient.netApi.podcasts
    }

    private fun provideNewsApi(): INews {
        return RetrofitClient.newsApi.news
    }

    private fun providePref(): Preference {
        if (preferecen != null) return preferecen!!
        else {
            preferecen =   PreferenceImp(application!!)
            return preferecen!!
        }
    }

    fun providePodcastRepository(): PodcastRepository {
        if (podcastRepository != null) return podcastRepository!!
        else {
            podcastRepository = PodcastRepository(
                provideDao(),
                providePodcastApi(),
                providePref(),
                provideOnlinePodcastLink()
            )
            return podcastRepository!!
        }
    }

    fun provideNewsRepository(): NewsRepository {
        if (newsRepository != null) return newsRepository!!
        else {
            newsRepository = NewsRepository(
                provideNewsApi()
            )
            return newsRepository!!
        }
    }

    fun provideAnalitic(): AnaliticManager {
        if (analictic != null) return analictic!!
        else {
            analictic = AnaliticManagerImpl(provideContext())
            return analictic!!
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