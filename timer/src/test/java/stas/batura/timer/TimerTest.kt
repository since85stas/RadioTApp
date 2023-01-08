package stas.batura.timer

import org.junit.Assert
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
        goalTime.set(Calendar.MILLISECOND, 0)
    }

    @Test
    fun check_hour_sec() {
        val timer = TimerImpl(goalTime)

        val calt = Calendar.getInstance()
        calt.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY )
        calt.set(Calendar.HOUR_OF_DAY, 22)
        calt.set(Calendar.MINUTE, 0)
        calt.set(Calendar.SECOND, 0)
        calt.set(Calendar.MILLISECOND, 0)

        val d = timer.getTimeDifference(calt, goalTime)
        Assert.assertEquals(1*60*60, d)
    }

    @Test
    fun check_hour_min() {
        val timer = TimerImpl(goalTime, TimerImpl.DurationType.MINUTES)

        val calt = Calendar.getInstance()
        calt.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY )
        calt.set(Calendar.HOUR_OF_DAY, 22)
        calt.set(Calendar.MINUTE, 0)
        calt.set(Calendar.SECOND, 0)
        calt.set(Calendar.MILLISECOND, 0)

        val d = timer.getTimeDifference(calt, goalTime)
        Assert.assertEquals(1*60, d)
    }

    @Test
    fun check_day_min() {
        val timer = TimerImpl(goalTime, TimerImpl.DurationType.MINUTES)

        val calt = Calendar.getInstance()
        calt.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY )
        calt.set(Calendar.HOUR_OF_DAY, 23)
        calt.set(Calendar.MINUTE, 0)
        calt.set(Calendar.SECOND, 0)
        calt.set(Calendar.MILLISECOND, 0)

        val d = timer.getTimeDifference(calt, goalTime)
        Assert.assertEquals(1*60*24, d)
    }

    @Test
    fun check_6day_min() {
        val timer = TimerImpl(goalTime, TimerImpl.DurationType.MINUTES)

        val calt = Calendar.getInstance()
        calt.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY )
        calt.set(Calendar.HOUR_OF_DAY, 23)
        calt.set(Calendar.MINUTE, 0)
        calt.set(Calendar.SECOND, 0)
        calt.set(Calendar.MILLISECOND, 0)

        val d = timer.getTimeDifference(calt, goalTime)
        Assert.assertEquals(1*60*24*6, d)
    }
}