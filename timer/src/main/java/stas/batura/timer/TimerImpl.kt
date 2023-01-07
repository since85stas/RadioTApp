package stas.batura.timer

import android.util.Log
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters
import java.util.*

const val TAG = "TimerImpl"

class TimerImpl: Timer {

    private val goalTime: Calendar

    init {
        goalTime = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"))
        goalTime.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY )
        goalTime.set(Calendar.HOUR_OF_DAY, 23)
        goalTime.set(Calendar.MINUTE, 0)
        goalTime.set(Calendar.SECOND, 0)


    }

    override fun getValues(): Flow<Int> {
        TODO("Not yet implemented")
    }

    override fun startTimer() {
        TODO("Not yet implemented")
    }

    override fun stopTimer() {
        TODO("Not yet implemented")
    }

    fun getTimeDifference(calendar1: Calendar, calendar2: Calendar) {
        Log.d(TAG, "getTimeDifference: ${calendar1.time} ${calendar2.time}")
    }
}