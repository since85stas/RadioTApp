package stas.batura.retrofit

import retrofit2.http.GET
import retrofit2.http.Path
import stas.batura.data.PodcastBody


interface IRetrofit {
    @GET("podcast/{num}")
    suspend fun getPodcastByNum(@Path("num") num: String):
            PodcastBody

    @GET("last/{N}?categories=podcast")
    suspend fun getLastNPodcasts(@Path("N") number: Int): List<PodcastBody>

    @GET("last/1?categories=podcast")
    suspend fun getLastPodcast(): List<PodcastBody>


}