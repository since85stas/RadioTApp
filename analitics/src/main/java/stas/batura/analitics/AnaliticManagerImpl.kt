package stas.batura.analitics

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics

class AnaliticManagerImpl(context: Context) {

    private val firebase = FirebaseAnalytics.getInstance(context)

    fun testEvent() {
        firebase.logEvent(FirebaseAnalytics.Event.LOGIN, null)
        firebase.logEvent(FirebaseAnalytics.Event.POST_SCORE, null)
    }
}