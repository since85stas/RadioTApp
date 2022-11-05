package stas.batura.radiotproject

import android.app.Application
import android.util.Log

class RadioApp(): Application() {

    init {
        Log.d(this::class.java.simpleName, "app: init ")
    }

}