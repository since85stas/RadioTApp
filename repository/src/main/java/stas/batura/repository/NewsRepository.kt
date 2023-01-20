package stas.batura.repository

import stas.batura.retrofit.INews
import timber.log.Timber

class NewsRepository(
    val newsApi: INews
) {

    suspend fun getLastNews() {
        val news = newsApi.getLastNNews(20)
        Timber.d("news: $news")
    }

}