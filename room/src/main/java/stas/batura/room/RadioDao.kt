package ru.batura.stat.batchat.repository.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import stas.batura.room.download.DownloadDao
import stas.batura.room.podcast.Podcast
import stas.batura.room.podcast.SavedStatus
import java.net.URL

@Dao
interface RadioDao: PodcastDao, DownloadDao {


}