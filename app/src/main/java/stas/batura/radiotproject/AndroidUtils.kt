package stas.batura.radiotproject

import java.text.SimpleDateFormat
import java.util.*

const val SAVE_FILE_FORM = "dd MMM yyyy"

fun createPodcastDateTitle(time: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = time
    val timeStamp = SimpleDateFormat(
        SAVE_FILE_FORM, Locale.getDefault()
    ).format(calendar.time)
    return timeStamp
}