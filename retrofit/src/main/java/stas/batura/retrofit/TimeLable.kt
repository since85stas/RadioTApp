package stas.batura.retrofit

import com.squareup.moshi.Json
import stas.batura.utils.DateTime

data class TimeLabel (

    @Json(name = "topic")
    val topic: String, // название темы

    @Json(name = "time")
    val startTime: String,    // время начала в RFC3339

    @Json(name = "duration")
    val duration: Int? = null,    // длительность в секундах

    var newStartTime: Long = 0L,

    var startTimeString: String = ""
) {
    /**
     * transform class field [time] to Milliseconds
     */
    public fun getMillisTime(): Long {
        val dateTime: DateTime = DateTime.parseRfc3339(startTime)
        val millis: Long = dateTime.getValue()
//        val millis = 10*1000*60L;
        return millis
    }
}