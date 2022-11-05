package stas.batura.radiotproject

import android.app.Application
import android.util.Log
import stas.batura.di.ServiceLocator

class RadioApp(): Application() {

    val res = 1

    init {
        Log.d(this::class.java.simpleName, "app: init ")
        ServiceLocator.setContext(this)
    }

}