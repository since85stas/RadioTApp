package stas.batura.repository

import kotlinx.coroutines.flow.Flow
import stas.batura.data.ListViewType
import stas.batura.data.SavedPodcast
import stas.batura.data.Year
import stas.batura.data.Podcast
import stas.batura.data.SavedStatus

interface IPodcastRepository {

    suspend fun tryUpdateRecentRadioCache()

    fun addPodcast(podcast: Podcast)

    suspend fun getActivePodcastSus(podcastId: Int): Podcast?

    fun updatePodcastLastPos(podcastId: Int, position: Long)

    fun setNumPodcsts(num: Int)

    fun getPrefActivePodcastNum(): Flow<Int>

    fun getPrefActivePodcast(): Flow<Podcast>

    fun setPrefActivePodcastNum(num: Int)

    fun setPrefListType(type: ListViewType)

    val podcastViewType: Flow<ListViewType>

    fun setPrefNumOnPage(num: Int)

    fun setPrefSelectedYear(year: Year)

    fun numberTypeList(lastId: Int): Flow<List<Podcast>>

    fun yearTypeList(): Flow<List<Podcast>>

    fun favTypeList(): Flow<List<Podcast>>

    fun getAllPodcastsList(): Flow<List<Podcast>>

    fun updateTrackDuration(podcastId: Int, duration: Long)

    fun updateTrackIdDetailed(podcastId: Int, isDetailed: Boolean)

    fun getTypeAndNumb(): Flow<PodcastLoadInfo>

    fun updateLastPodcPrefsNumber()

    suspend fun changeLastPnumberByValue(num: Int)

    fun setFavoriteStatus(podcastId: Int, status: Boolean)

    fun updatePodcastSavedStatus(podcastId: Int,savedStatus: SavedStatus)

    fun setPodcastToSaved(podcastId: Int, localPath:String)

    suspend fun getPodcastLocalPath(podcastId: Int): String

    fun deletePodcastCahe(podcastId: Int)

    val savedPodcasts: Flow<List<SavedPodcast>>

    suspend fun addMorePodcasts()

}
