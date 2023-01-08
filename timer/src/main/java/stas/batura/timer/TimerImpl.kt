package stas.batura.timer

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters
import java.util.*

const val TAG = "TimerImpl"

class TimerImpl(private val goalTime: Calendar, val durType: DurationType = DurationType.SECONDS): Timer {

    init {
    }

    override fun getValues(): Flow<Long> {
        val flow = flow {
            val diff = getTimeDifference(goalTime)
            for (i in diff downTo 0) {
                emit(i)
                kotlinx.coroutines.delay(durType.mult.toLong())
            }

        }
        return flow
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