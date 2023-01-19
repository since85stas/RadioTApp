package stas.batura.data

data class NewsBody(
    var title: String,

    var content:  String,

    var snippet: List<String>,

    var pic: String? = null,

    var link:   String? = null,

    var author: String? = null,

    var geek: Boolean = false,

    var votes: Int = 0,

    var del: Boolean = false
)
