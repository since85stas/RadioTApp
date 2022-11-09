package stas.batura.room.download

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "download_table")
data class PodcastDownload(
    @PrimaryKey
    val podcastId: Int,
    val localPath: String,
)
