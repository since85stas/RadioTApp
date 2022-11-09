package stas.batura.download

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.tonyodev.fetch2okhttp.OkHttpDownloader
import okhttp3.OkHttpClient
import stas.batura.di.ServiceLocator
import timber.log.Timber
import android.os.Build
import android.R
import android.app.*
import android.content.Context
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.tonyodev.fetch2.*
import java.lang.Error

class DownloadService(): Service(), DownloadCommands {

    val CHANNEL_ID = "ForegroundServiceChannel"

    val NOTIF_ID = 44

    private var downloadId: Int? = null

    private var cachedPath: String? = null

    val notificationBulder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Notification.Builder(ServiceLocator.provideContext(), CHANNEL_ID)
            .setContentTitle("Downloading...")
            .setSmallIcon(R.drawable.arrow_down_float)

    } else {
        Timber.d("start")
        Notification.Builder(ServiceLocator.provideContext())
            .setContentTitle("Downloading...")
            .setSmallIcon(R.drawable.arrow_down_float)

    }

    // this will be initialized lazily
    private val notificationManager by lazy {
        ServiceLocator.provideContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    protected var downloadLink: String? = null

    protected val fetch: Fetch = initDownloader()

    init {
        Timber.d("Service start")
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()


        startForeground(NOTIF_ID, notificationBulder.build())
        //do heavy work on a background thread
        //stopSelf()

        val link = intent?.extras?.getString(LINK_KEY)
        downloadId = intent?.extras?.getInt(PODCAST_ID, 0)
        startDownload(link!!, downloadId!!)

        //do heavy work on a background thread
        //stopSelf();
        return START_NOT_STICKY
    }

    override fun progress(preogressPercent: Int) {
        Timber.d(preogressPercent.toString())
        val notification = notificationBulder.setContentText("Downloading... progress: ${preogressPercent}")
        notificationManager.notify(NOTIF_ID, notification.build())

    }

    override fun sendMessage(result: DownloadResult) {

        // отправляем в интент данные о пути к скачанному файлу
        val intent = Intent(DOWNLOAD_RESULT)
//        intent.putExtra(PODCAST_ID, downloadId)
//        intent.putExtra(CACHED_PATH, cachedPath)
        val resultS = DownloadServiceResult(downloadId!!, DownloadResult.OK(), cachedPath!!)
        intent.putExtra(CACHED_PATH, resultS)

        val local = LocalBroadcastManager.getInstance(this)
        when(result) {
            is DownloadResult.OK -> {
                val res = local.sendBroadcast(intent)
                stopSelf()
            }
            is DownloadResult.Error -> {
                val res = local.sendBroadcast(intent)
                stopSelf()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return DownloadServiceBinder()
    }

    inner class DownloadServiceBinder : Binder() {

    }

    /**
     * инициализируем загрузчик для медиа
     */
    private fun initDownloader(): Fetch {
        val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()

        val fetchConfiguration: FetchConfiguration = FetchConfiguration.Builder(ServiceLocator.provideContext())
            .setProgressReportingInterval(1000)
            .setHttpDownloader(OkHttpDownloader(okHttpClient))
            .enableHashCheck(true)
            .setAutoRetryMaxAttempts(10)
            .enableRetryOnNetworkGain(enabled = true)
            .build()

        val dm = Fetch.Impl.getInstance(fetchConfiguration)
        dm.addListener(DownloadHandler(this))
        return dm
    }

    private fun startDownload(link: String, id: Int) {
        cachedPath = ServiceLocator.providePodcastAudioCacheDir() + "audio_$id.mp3"
        val request = Request(link, cachedPath!!)
        request.tag = link
        request.priority = Priority.HIGH
//        request.extras = Extras(mapOf(TYPE_EXTRA to TYPE_CONTENT))
        request.networkType = NetworkType.ALL

        fetch.enqueue(
            request, {}, {}
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

    companion object {
        val LINK_KEY = "link"
        val PODCAST_ID = "podcast_id"
        val CACHED_PATH = "podcast_id"
        val DOWNLOAD_RESULT = "stas.batura.download.result"
    }

}