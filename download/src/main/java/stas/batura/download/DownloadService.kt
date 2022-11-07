package stas.batura.download

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.FetchConfiguration
import com.tonyodev.fetch2okhttp.OkHttpDownloader
import okhttp3.OkHttpClient
import stas.batura.di.ServiceLocator

class DownloadService(): Service(), DownloadCommands {

    protected var downloadLink: String? = null

    protected val fetch: Fetch = initDownloader()

    init {

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

}