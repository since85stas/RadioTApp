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
