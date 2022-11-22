package stas.batura.room.download

import androidx.room.*

@Dao
interface DownloadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(podcastDownload: PodcastDownload)

    @Query("SELECT localPath FROM download_table WHERE podcastId = :podcastId")
    suspend fun getPodcastLocalPath(podcastId: Int): String

    @Query("DELETE FROM download_table WHERE podcastId = :podcastId")
    suspend fun deletePodcastFromCache(podcastId: Int)
}