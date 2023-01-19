package stas.batura.retrofit

import retrofit2.http.GET
import retrofit2.http.Path
import stas.batura.data.NewsBody
import stas.batura.data.PodcastBody

interface INews {

    @GET("GET /news/last/{N} ")
    suspend fun getLastNNews(@Path("N") number: Int): List<NewsBody>

}