package stas.batura.radioproject.data

import kotlinx.coroutines.flow.Flow
import stas.batura.data.ListViewType
import stas.batura.data.Year
import stas.batura.room.podcast.Podcast
import stas.batura.room.podcast.SavedStatus

interface IRepository {

    suspend fun tryUpdateRecentRadioCache()

    fun addPodcast(podcast: Podcast)

    suspend fun getActivePodcastSus(podcastId: Int): Podcast?

    fun updatePodcastLastPos(podcastId: Int, position: Long)

    fun setNumPodcsts(num: Int)

    fun getPrefActivePodcastNum(): Flow<Int>

    fun getPrefActivePodcast(): Flow<Podcast>

    fun setPrefActivePodcastNum(num: Int)

    fun setPrefListType(type: ListViewType)

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

    suspend fun updateRedrawField(podcastId: Int)

    fun setFavoriteStatus(podcastId: Int, status: Boolean)

    fun updatePodcastSavedStatus(podcastId: Int,savedStatus: SavedStatus)

    suspend fun getPodcastLocalPath(podcastId: Int): String

    fun deletePodcastCahe(podcastId: Int)
}
