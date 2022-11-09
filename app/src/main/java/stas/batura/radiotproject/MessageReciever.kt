package stas.batura.radiotproject

import android.content.Intent

import android.content.BroadcastReceiver
import android.content.Context
import stas.batura.download.DownloadService
import stas.batura.download.DownloadServiceResult
import timber.log.Timber

class MessageReceiver(recieverResult: RecieverResult) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            val cahedPath = intent.getSerializableExtra(DownloadService.CACHED_PATH)
            val downloadResult = cahedPath as DownloadServiceResult
            Timber.d("result $downloadResult")
        }
    }
}