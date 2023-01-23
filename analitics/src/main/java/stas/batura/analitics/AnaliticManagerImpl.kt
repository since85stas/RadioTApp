package stas.batura.analitics

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics

interface AnaliticManager {
    fun playEvent()
    fun onlineEvent()
    fun downloadEvent()
    fun errorPlayEvent()
    fun appOpenEvent()
    fun newsEvent()
    fun addOldPodcast()
}

class AnaliticManagerImpl(context: Context) : AnaliticManager {

    private val firebase = FirebaseAnalytics.getInstance(context)

    override fun playEvent() {
        firebase.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, null)
    }

    override fun onlineEvent() {
        firebase.logEvent("SELECT_ONLINE", null)
    }

    override fun downloadEvent() {
        firebase.logEvent("DOWNLOAD", null)
    }

    override fun errorPlayEvent() {
        firebase.logEvent("PLAY_ERROR", null)
    }

    override fun appOpenEvent() {
        firebase.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)
    }

    override fun newsEvent() {
        firebase.logEvent("SELECT_NEWS", null)
    }

    override fun addOldPodcast() {
        firebase.logEvent("ADD_OLD", null)
    }
}