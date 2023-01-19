package stas.batura.data

data class NewsBody(
    var title: String,  // заголовок поста
    var content:  String,  // дата-время поста в RFC3339
    var snippet: List<String>, // список категорий, массив строк

    var pic: String? = null,

    var link:   String? = null,

    var author: String? = null,

    var geek: Boolean = false,

    var votes: Int = 0,

    var del: Boolean = false
)
