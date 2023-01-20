package stas.batura.repository

import kotlinx.coroutines.*
import stas.batura.retrofit.INews
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class NewsRepository(
    val newsApi: INews
) {

    suspend fun updateNews() {
        val news = newsApi.getLastNNews(20)
        Timber.d("news: $news")
    }

}