package stas.batura.analitics

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics

class AnaliticManagerImpl(context: Context) {

    private val firebase = FirebaseAnalytics.getInstance(context)

    fun testEvent() {
        firebase.logEvent(FirebaseAnalytics.Event.LOGIN, null)
        firebase.logEvent(FirebaseAnalytics.Event.POST_SCORE, null)
    }

    fun playEvent() {
        firebase.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, null)
    }

    fun onlineEvent() {

    }

    fun downloadEvent() {

    }

    fun errorPlayEvent() {
        firebase.logEvent(FirebaseAnalytics.Event.LOGIN, null)
    }

    fun appOpenEvent() {
        firebase.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)
    }

}