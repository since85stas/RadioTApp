package stas.batura.download

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class DownloadService(): Service() {


    init {

    }

    override fun onBind(intent: Intent?): IBinder? {
        return DownloadServiceBinder()
    }

    inner class DownloadServiceBinder : Binder() {

    }

}