package stas.batura.radiotproject

import android.content.Intent

import android.content.BroadcastReceiver
import android.content.Context
import stas.batura.download.DownloadService
import stas.batura.download.DownloadServiceResult
import timber.log.Timber

class MessageReceiver(val recieverResult: RecieverResult) : BroadcastReceiver() {

    private var registered = false

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            val cahedPath = intent.getSerializableExtra(DownloadService.CACHED_PATH)
            val downloadResult = cahedPath as DownloadServiceResult
            Timber.d("result $downloadResult")
            recieverResult.donloadPodcastRsult(downloadResult)
        }
    }

//    fun register(context: Context) {
//        if (!registered) {
//            val filter = IntentFilter()
//            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED)
//            context.registerReceiver(this, filter)
//            registered = true
//        }
//    }

    fun unregister(context: Context) {
        if (registered) {
            context.unregisterReceiver(this)
            registered = false
        }
    }
}