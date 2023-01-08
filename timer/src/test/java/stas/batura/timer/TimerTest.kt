package stas.batura.timer

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TimerTest {

    val goalTime = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"))

    @Before
    fun bef() {
//            goalTime = Calendar.getInstance()
        goalTime.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY )
        goalTime.set(Calendar.HOUR_OF_DAY, 23)
        goalTime.set(Calendar.MINUTE, 0)
        goalTime.set(Calendar.SECOND, 0)
    }

    @Test
    fun addition_isCorrect() {
        val timer = TimerImpl(goalTime)
        
        timer.getTimeDifference(Calendar.getInstance())
    }
}