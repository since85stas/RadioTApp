package stas.batura.radiotproject

import stas.batura.room.podcast.Podcast
import java.text.SimpleDateFormat
import java.util.*

const val SAVE_FILE_FORM = "yyyy-MM-dd"

fun createPodcastDateTitle(time: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = time
    val timeStamp = SimpleDateFormat(
        SAVE_FILE_FORM, Locale.getDefault()
    ).format(calendar.time)
    return timeStamp
}