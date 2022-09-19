package ru.batura.stat.batchat.repository.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import stas.batura.room.podcast.Podcast
import stas.batura.room.podcast.SavedStatus
import java.net.URL

@Dao
interface PodcastDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPodcast(podcast: Podcast): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(plants: List<Podcast>)

    @Query("SELECT * FROM podcast_table ORDER BY podcastId DESC")
    fun getAllPodcastsList(): Flow<List<Podcast>>

    @Query("SELECT * FROM podcast_table WHERE isFavorite = 1 ORDER BY podcastId DESC")
    fun getFavoritesPodcastsList(): Flow<List<Podcast>>

    @Query("SELECT * FROM podcast_table ORDER BY podcastId DESC LIMIT :num")
    fun getLastNPodcastsList(num: Int): Flow<List<Podcast>>

    @Query("SELECT * FROM podcast_table WHERE timeMillis > :time ORDER BY podcastId DESC LIMIT :num")
    fun getNPodcastsListHighFromCurrent(num: Int, time: Long): Flow<List<Podcast>>

    @Query("SELECT * FROM podcast_table WHERE timeMillis < :time ORDER BY podcastId DESC LIMIT :num")
    fun getNPodcastsListLowFromCurrent(num: Int, time: Long): Flow<List<Podcast>>

    @Query("SELECT * FROM podcast_table WHERE timeMillis > :timeStart AND timeMillis < :timeEnd ORDER BY podcastId DESC")
    fun getPodcastsBetweenTimes(timeStart: Long, timeEnd: Long): Flow<List<Podcast>>

    @Query("SELECT * FROM podcast_table WHERE podcastId <= :lastId ORDER BY podcastId DESC LIMIT :num")
    fun getNPodcastsListBeforeId(num: Int, lastId: Int): Flow<List<Podcast>>

    @Query("SELECT * FROM podcast_table WHERE podcastId = :num")
    fun getPodcastFlowByNum (num: Int): Flow<Podcast>

    @Query("SELECT * FROM podcast_table WHERE podcastId = :num")
    suspend fun getPodcastByNum (num: Int): Podcast?

    @Query("SELECT * FROM podcast_table ORDER BY podcastId DESC")
    suspend fun getLastPodcast(): Podcast?

    @Query("UPDATE podcast_table SET isActive = 1 WHERE podcastId = :podcastId")
    suspend fun setPodcastActive(podcastId: Int)

    @Query ("UPDATE podcast_table SET isActive = 0 WHERE podcastId = :podcastId")
    suspend fun setPodIsNOTActive (podcastId: Int)

    @Query ("UPDATE podcast_table SET isFavorite = :status WHERE podcastId = :podcastId")
    suspend fun setPodFavoriteStatus (podcastId: Int, status: Boolean)

    @Query ("UPDATE podcast_table SET isActive = 0")
    suspend fun setAllPodIsNOTActive()

    @Query ("SELECT * FROM podcast_table WHERE isActive = 1")
    fun getActivePodcast(): Flow<Podcast?>

    @Query("UPDATE podcast_table SET isFinish = 1 WHERE podcastId = :podcastId")
    suspend fun setPodcastFinish(podcastId: Int)

    @Query("UPDATE podcast_table SET lastPosition = :position WHERE podcastId = :podcastId")
    suspend fun updatePodcastLastPos(podcastId: Int, position: Long)

    @Query("UPDATE podcast_table SET durationInMillis =:duration WHERE podcastId =:podcastId")
    suspend fun updateTrackDuration(podcastId: Int, duration: Long)

    @Query("UPDATE podcast_table SET isDetailed =:isDetailed WHERE podcastId =:podcastId")
    suspend fun updateTrackIdDetailed(podcastId: Int, isDetailed: Boolean)

    @Query("UPDATE podcast_table SET redraw = redraw +1 WHERE podcastId =:podcastId")
    suspend fun updateRedrawField(podcastId: Int)

    @Query("UPDATE podcast_table SET savedStatus= :savedStatus WHERE podcastId =:podcastId")
    suspend fun updatePodcastSavedStatus(podcastId: Int ,savedStatus: SavedStatus)
}