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
import com.tonyodev.fetch2.*

class DownloadService(): Service(), DownloadCommands {

    val CHANNEL_ID = "ForegroundServiceChannel"

    val notificationBulder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Downloading...")
            .setSmallIcon(R.drawable.arrow_down_float)

    } else {
        Timber.d("start")
        Notification.Builder(this)
            .setContentTitle("Downloading...")
            .setSmallIcon(R.drawable.arrow_down_float)

    }

    // this will be initialized lazily
    private val notificationManager: NotificationManager by lazy {
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


        startForeground(1, notificationBulder.build())
        //do heavy work on a background thread
        //stopSelf()

        val link = intent?.extras?.getString(LINK_KEY)
        startDownload(link!!)

        //do heavy work on a background thread
        //stopSelf();
        return START_NOT_STICKY
    }

    override fun progress(preogressPercent: Int) {
        Timber.d(preogressPercent.toString())
        val notification = notificationBulder.setContentText("Downloading... progress: ${preogressPercent}")
        notificationManager.notify(1, notification.build())

    }

    override fun sendMessage() {

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

    private fun startDownload(link: String) {
        val cachePath = ServiceLocator.providePodcastAudioCacheDir() + "test_name.mp3"
        val request = Request(link, cachePath)
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
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

    companion object {
        val LINK_KEY = "link"
    }

}