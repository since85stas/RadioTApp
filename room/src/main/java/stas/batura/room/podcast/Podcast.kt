package stas.batura.room.podcast

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.android.exoplayer2.Timeline
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import stas.batura.radioproject.data.ListViewType
import stas.batura.radioproject.data.dataUtils.DateTime
import stas.batura.radioproject.data.dataUtils.TIME_WEEK
import stas.batura.radioproject.data.dataUtils.getLinksFromHtml
import stas.batura.radioproject.data.dataUtils.getMillisTime
import stas.batura.radioproject.data.net.PodcastBody
import stas.batura.radioproject.data.net.TimeLabel


@Entity(tableName = "podcast_table")
data class Podcast(
    @PrimaryKey()
    val podcastId: Int,

    // url поста
    val url: String = "url",

    // заголовок поста
    val title: String = "title",

    // дата-время поста в RFC3339
    val time: String = "0",

    var timeMillis: Long = 0L,

    val categories: List<String>? = null,

    var imageUrl: String? = null,

    var fileName: String? = null,

    var bodyHtml: List<String>? = null,

    var postText: String? = null,

    var audioUrl: String? = null,

    var timeLabels: List<TimeLabel>? = null,

    var isActive: Boolean = false,

    var isFinish: Boolean = false,

    var lastPosition: Long = 0,

    var durationInMillis: Long = 0,

    var isDetailed: Boolean = false,

    var isPlaying: Boolean = false,

    var redraw: Int = 0,

    var isFavorite: Boolean = false,

    var savedStatus: SavedStatus = SavedStatus.NOT_SAVED

//    var localImageUrl: String? = null
) {

    object FromPodcastBody {

        fun build(podcastBody: PodcastBody): Podcast {

            // убираем все буквы, оставляем только номер
            val reg = "\\D".toRegex()
            val num = reg.replace(podcastBody.title, "")

            return Podcast(
                num.toInt(),
                podcastBody.url,
                podcastBody.title,
                podcastBody.date.toString(),
                getMillisTime(podcastBody.date),
                podcastBody.categories,
                podcastBody.imageUrl,
                podcastBody.fileName,
                getLinksFromHtml(podcastBody.bodyHtml, podcastBody.timeLables?.size),
                podcastBody.postText,
                podcastBody.audioUrl,
                fillTimelable(podcastBody.timeLables)
            )
        }

    }

    /**
     * check if week is passed after [newTime] value
     */
    fun isWeekGone(newTime: Long): Boolean {
        if (newTime - getMillisTime(time) > TIME_WEEK) {
            return true
        } else {
            return false
        }
    }

    /**
     * check if week is passed after [newTime] value
     */
    fun numWeekGone(newTime: Long): Int {
        if (newTime - getMillisTime(time) > TIME_WEEK) {
            val del = (newTime - getMillisTime(time))/ TIME_WEEK
            return del.toInt()
        } else {
            return 0
        }
    }

    /**
     * get played duration of track in percents
     */
    fun getPlayedInPercent(): Int {
        val pos = lastPosition.toDouble()
        val dur = durationInMillis.toDouble()
        if (pos>dur) return 100
        return if (durationInMillis == 0L) 0 else (Math.round(pos / dur * 100.0f)).toInt()
    }


    override fun toString(): String {
        return "Podcast $podcastId $url $title $lastPosition $durationInMillis"
    }
}

class CategoryDataConverter {

    @TypeConverter()
    fun fromCountryLangList(value: List<String>): String {
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toCountryLangList(value: String): List<String> {
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type)
    }

}

enum class SavedStatus(status: Byte) {
    NOT_SAVED(0),
    SAVED(1),
    LOADING(2),
    ERROR(3);

    companion object {
        private val VALUES = SavedStatus.values()

        fun getByValue(value: Byte) = VALUES.firstOrNull { it.ordinal.toByte() == value }
    }
}

class SavedStatusConverter {

    @TypeConverter()
    fun fromStatusToByte(status: SavedStatus?): Byte? {
        if (status != null) {
            return status.ordinal.toByte()
        } else {
            return null
        }
    }

    @TypeConverter
    fun fromByteToStatus(status: Byte?): SavedStatus? {
        if (status != null) {
            return SavedStatus.getByValue(status)
        } else {
            return null
        }
    }

}



