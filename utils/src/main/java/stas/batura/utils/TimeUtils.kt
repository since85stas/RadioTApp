package stas.batura.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

val TIME_WEEK = 7*24*60*60*1000L

val TAG = "TimeUtils.kt"

/**
 * transform class field [time] to Milliseconds
 */
fun getMillisTime(time: String): Long {
    val dateTime: DateTime = DateTime.parseRfc3339(time)
    val millis: Long = dateTime.getValue()
    return millis
}

enum class Year(val yearS: Long, val yearE: Long) {
    Y2022(getYearStart(2022), getYearEnd(2022)),
    Y2021(getYearStart(2021), getYearEnd(2021)),
    Y2020(getYearStart(2020), getYearEnd(2020)),
    Y2019(getYearStart(2019), getYearEnd(2019)),
    Y2018(getYearStart(2018), getYearEnd(2018)),
    Y2017(getYearStart(2017), getYearEnd(2017)),
    Y2016(getYearStart(2016), getYearEnd(2016)),
    Y2015(getYearStart(2015), getYearEnd(2015)),
    Y2014(getYearStart(2014), getYearEnd(2014)),
    Y2013(getYearStart(2013), getYearEnd(2013));

    companion object {
        private val VALUES = Year.values()

        fun getByValue(value: Int) = VALUES.firstOrNull { it.ordinal == value }
    }
}

fun getYearStart(year: Int): Long {

    val calendar: Calendar = GregorianCalendar()
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, Calendar.JANUARY)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 10)

//    Log.d(TAG, "getYearStart: ${getTimeFormat(calendar)}")

    return calendar.timeInMillis
}

fun getYearEnd(year: Int): Long {

    val calendar: Calendar = GregorianCalendar()
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, Calendar.DECEMBER)
    calendar.set(Calendar.DAY_OF_MONTH, 31)
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 50)

//    Log.d(TAG, "getYearУтв: ${getTimeFormat(calendar)}")

    return calendar.timeInMillis
}

//fun getTimeFormat(calendar: Calendar): String {
//    val formatter = SimpleDateFormat("dd/MM/YYYY HH:mm");
//    val dateString = formatter.format( Date(calendar.timeInMillis))
//    return dateString
//}