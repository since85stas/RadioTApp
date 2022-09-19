package stas.batura.room.podcast

import android.widget.TextView
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import stas.batura.retrofit.TimeLabel
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/** Regular expression for parsing RFC3339 date/times.  */
private val PATTERN = Pattern.compile(
            "((\\d{2}):(\\d{2}):(\\d{2}))" // 'T'HH:mm:ss.milliseconds
) // 'Z' or time zone shift HH:mm following '+' or '-'


fun fillTimelable(timelables: List<TimeLabel>?): List<TimeLabel>? {

    var newTimeLables: MutableList<TimeLabel>? = mutableListOf()

    if (timelables != null) {
        var start = 0L
        // using regex to find start time
        val startTime: String = timelables[0].startTime
        val regex = PATTERN.toRegex()
        val res = regex.find(startTime)!!.value

        val digits = res.split(":")
        if (digits.size > 2 ) {
            start = (digits[1].toInt() * 60 + digits[2].toInt()) * 1000L
        }

        for (lable in timelables) {
            val newLable = lable
            newLable.newStartTime = start
            newLable.startTimeString = setTrackDuratNative(start)
            lable.duration?.let {
                start = start + it * 1000L
            }

            newTimeLables!!.add(newLable)
        }
        return newTimeLables
    } else {
        return null
    }

}

class TimeLabelsDataConverter {

    @TypeConverter()
    fun fromTimeLableList(value: List<TimeLabel>): String {
        val gson = Gson()
        val type = object : TypeToken<List<TimeLabel>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toTimeLableList(value: String): List<TimeLabel> {
        val gson = Gson()
        val type = object : TypeToken<List<TimeLabel>>() {}.type
        return gson.fromJson(value, type)
    }

}


fun setTrackDurat (dur: Long): String {

    val formatter = SimpleDateFormat("HH:mm:ss", Locale.GERMAN);
    val dateString = formatter.format( dur );
   return dateString
}

fun setTrackDuratNative(dur: Long): String {
    val timeInSec = dur/1000
//    val timeSec = dur/1000
//    val timeMin = timeSec/1000
    val timeHourPr = timeInSec/60/60
    val timeMin = timeInSec - timeHourPr*60*60
    val timeMinPr = timeMin/60
    val timeSec = timeMin - timeMinPr*60
//    val timeSecPr =
    val timeStr = StringBuilder()
    if (timeHourPr < 10) {
        timeStr.append("0$timeHourPr")
    } else {
        timeStr.append("$timeHourPr")
    }
    timeStr.append(":")

    if (timeMinPr < 10) {
        timeStr.append("0$timeMinPr")
    } else {
        timeStr.append("$timeMinPr")
    }
    timeStr.append(":")

    if (timeSec < 10) {
        timeStr.append("0$timeSec")
    } else {
        timeStr.append("$timeSec")
    }
    return timeStr.toString()
}