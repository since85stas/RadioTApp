package stas.batura.timer

import android.util.Log
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters
import java.util.*

const val TAG = "TimerImpl"

class TimerImpl(private val goalTime: Calendar, val durType: DurationType = DurationType.SECONDS): Timer {

    init {
//        goalTime = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"))
//        goalTime.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY )
//        goalTime.set(Calendar.HOUR_OF_DAY, 23)
//        goalTime.set(Calendar.MINUTE, 0)
//        goalTime.set(Calendar.SECOND, 0)


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

    fun getTimeDifference(now: Calendar, goalTimeF: Calendar = goalTime): Long {

        if (goalTimeF.before(now)) {
            goalTimeF.add(Calendar.DAY_OF_WEEK, 7)
        }

        println("getTimeDifference: ${now.time} ${goalTime.time}")

        val diff = (goalTimeF.timeInMillis - now.timeInMillis)/durType.mult
        return diff
    }

    enum class DurationType(val mult: Int) {
        MILLIS(1),
        SECONDS(1000),
        MINUTES(1000*60)
    }
}