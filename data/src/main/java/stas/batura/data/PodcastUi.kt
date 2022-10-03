package stas.batura.data

import stas.batura.retrofit.TimeLabel

data class PodcastUi(
    val podcastId: Int,

    // url поста
    val url: String = "url",

    // заголовок поста
    val title: String = "title",

    // дата-время поста в RFC3339
    val time: String = "0",

    var timeMillis: Long = 0L,

    val categories: List<String>? = null,

    var imageUrl: String? = null,

    var fileName: String? = null,

    var bodyHtml: List<String>? = null,

    var postText: String? = null,

    var audioUrl: String? = null,

    var timeLabels: List<TimeLabel>? = null,

    var isActive: Boolean = false,

    var isFinish: Boolean = false,

    var lastPosition: Long = 0,

    var durationInMillis: Long = 0,

    var isDetailed: Boolean = false,

    var isPlaying: Boolean = false,

    var redraw: Int = 0,

    var isFavorite: Boolean = false,

    var savedStatus: Boolean = false,

//    var localImageUrl: String? = null
)