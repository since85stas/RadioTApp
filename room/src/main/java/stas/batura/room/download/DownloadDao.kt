package stas.batura.room.download

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DownloadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(podcastDownload: PodcastDownload)

    @Query("SELECT localPath FROM download_table WHERE podcastId = :podcastId")
    suspend fun getPath(podcastId: Int): String


}