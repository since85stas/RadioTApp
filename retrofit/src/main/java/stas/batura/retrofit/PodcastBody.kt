package stas.batura.retrofit

import com.squareup.moshi.Json
import stas.batura.utils.DateTime


data class PodcastBody (
    var url: String  ,  // url поста
    var title: String,  // заголовок поста
    var date:  String,  // дата-время поста в RFC3339
    var categories: List<String>, // список категорий, массив строк

    @Json(name = "image")
    var imageUrl: String? = null,    // url картинки

    @Json(name = "file_name")
    var fileName:   String? = null,  // имя файла

    @Json(name = "body")
    var bodyHtml: String? = null,     // тело поста в HTML

    @Json(name = "show_notes")
    var postText: String? = null,  // пост в текстовом виде

    @Json(name = "audio_url")
    var audioUrl: String? = null,   // url аудио файла

    @Json(name = "time_labels")
    var timeLables: List<TimeLabel>? = null) // массив временых меток тем

data class TimeLabel (
    val topic: String, // название темы

    @Json(name = "time")
    val startTime: String,    // время начала в RFC3339

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

