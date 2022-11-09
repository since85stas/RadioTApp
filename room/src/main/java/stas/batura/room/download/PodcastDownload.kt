package stas.batura.room.download

import androidx.room.Entity

@Entity(tableName = "download_table")
data class PodcastDownload(
    val podcastId: Int,
    val localPath: String,
)
