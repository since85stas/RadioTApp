package stas.batura.data

import com.squareup.moshi.Json
import stas.batura.retrofit.TimeLabel

data class NewsBody(
    var title: String,  // заголовок поста
    var content:  String,  // дата-время поста в RFC3339
    var snippet: List<String>, // список категорий, массив строк

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
)
