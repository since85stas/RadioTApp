package stas.batura.data

import com.squareup.moshi.Json

data class NewsBody(
    var title: String,

    var content:  String,

    var snippet: String,

    var pic: String? = null,

    var link:   String? = null,

    var author: String? = null,

    var geek: Boolean = false,

    var votes: Int = 0,

    var del: Boolean = false,

    var comments: Int = 0,

    @Json(name = "show_num")
    var podcastId: Int = 0,
)
