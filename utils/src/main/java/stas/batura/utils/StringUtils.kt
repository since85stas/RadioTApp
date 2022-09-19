package stas.batura.utils

import android.util.Log


fun getLinksFromHtml(body: String?, size: Int?): List<String>? {

    if (body != null && size != null) {

        // убираем все буквы, оставляем только номер
        val reg = "http[^\"]*".toRegex()
//        val reg = ".*a$".toRegex()
        val num = reg.findAll(body)
        val citesList = mutableListOf<String>()
        for (result in num) {
//            Log.d("StringUtils", "getLinksFromHtml: " + result.value)
            citesList.add(result.value)
        }
        val croped = citesList.drop(1).take(size)
//        Log.d("StringUtils", "getLinksFromHtml:")
        return croped
    }

    return null
}