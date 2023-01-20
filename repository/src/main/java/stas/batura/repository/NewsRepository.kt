package stas.batura.repository

import stas.batura.data.NewsBody
import stas.batura.retrofit.INews
import timber.log.Timber

class NewsRepository(
    val newsApi: INews
) {

    suspend fun getLastNews(): List<NewsBody> {
        val news = newsApi.getLastNNews(20)
        return news
    }

}