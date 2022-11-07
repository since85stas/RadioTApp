package stas.batura.download

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.FetchConfiguration
import com.tonyodev.fetch2okhttp.OkHttpDownloader
import okhttp3.OkHttpClient
import stas.batura.di.ServiceLocator
import timber.log.Timber

import android.os.Build
import com.google.android.exoplayer2.offline.DownloadService.startForeground

import android.R
import android.app.*

class DownloadService(): Service(), DownloadCommands {

    val CHANNEL_ID = "ForegroundServiceChannel"



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

        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Downloading...")
                .setSmallIcon(R.drawable.arrow_down_float)
                .build()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        startForeground(1, notification)
        //do heavy work on a background thread
        //stopSelf()

        val link = intent?.extras?.getString(LINK_KEY)

        //do heavy work on a background thread
        //stopSelf();
        return START_NOT_STICKY
    }

    override fun progress(preogressPercent: Int) {

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