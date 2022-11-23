package stas.batura.room.download

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(podcastDownload: PodcastDownload)

    @Query("SELECT localPath FROM download_table WHERE podcastId = :podcastId")
    suspend fun getPodcastLocalPath(podcastId: Int): String

    @Query("DELETE FROM download_table WHERE podcastId = :podcastId")
    suspend fun deletePodcastFromCache(podcastId: Int)

    @Query("SELECT * FROM download_table")
    fun getAllSavedData(): Flow<List<PodcastDownload>>
}