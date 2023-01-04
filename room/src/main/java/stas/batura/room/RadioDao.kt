package stas.batura.room

import androidx.room.Dao
import stas.batura.room.download.DownloadDao
import stas.batura.room.podcast.PodcastDao

@Dao
interface RadioDao: PodcastDao, DownloadDao {


}