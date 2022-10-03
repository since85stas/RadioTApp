package stas.batura.room.podcast

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import stas.batura.data.PodcastBody

import stas.batura.retrofit.TimeLabel
import stas.batura.utils.TIME_WEEK
import stas.batura.utils.getLinksFromHtml
import stas.batura.utils.getMillisTime
import java.lang.StringBuilder
import java.util.regex.Pattern


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

/** Regular expression for parsing RFC3339 date/times.  */
private val PATTERN = Pattern.compile(
    "((\\d{2}):(\\d{2}):(\\d{2}))" // 'T'HH:mm:ss.milliseconds
) // 'Z' or time zone shift HH:mm following '+' or '-'


fun fillTimelable(timelables: List<TimeLabel>?): List<TimeLabel>? {

    var newTimeLables: MutableList<TimeLabel>? = mutableListOf()

    if (timelables != null) {
        var start = 0L
        // using regex to find start time
        val startTime: String = timelables[0].startTime
        val regex = PATTERN.toRegex()
        val res = regex.find(startTime)!!.value

        val digits = res.split(":")
        if (digits.size > 2 ) {
            start = (digits[1].toInt() * 60 + digits[2].toInt()) * 1000L
        }

        for (lable in timelables) {
            val newLable = lable
            newLable.newStartTime = start
            newLable.startTimeString = setTrackDuratNative(start)
            lable.duration?.let {
                start = start + it * 1000L
            }

            newTimeLables!!.add(newLable)
        }
        return newTimeLables
    } else {
        return null
    }

}

fun setTrackDuratNative(dur: Long): String {
    val timeInSec = dur/1000
//    val timeSec = dur/1000
//    val timeMin = timeSec/1000
    val timeHourPr = timeInSec/60/60
    val timeMin = timeInSec - timeHourPr*60*60
    val timeMinPr = timeMin/60
    val timeSec = timeMin - timeMinPr*60
//    val timeSecPr =
    val timeStr = StringBuilder()
    if (timeHourPr < 10) {
        timeStr.append("0$timeHourPr")
    } else {
        timeStr.append("$timeHourPr")
    }
    timeStr.append(":")

    if (timeMinPr < 10) {
        timeStr.append("0$timeMinPr")
    } else {
        timeStr.append("$timeMinPr")
    }
    timeStr.append(":")

    if (timeSec < 10) {
        timeStr.append("0$timeSec")
    } else {
        timeStr.append("$timeSec")
    }
    return timeStr.toString()
}

class CategoryDataConverter {

    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val type = Types.newParameterizedType(List::class.java, String::class.java)

    @TypeConverter()
    fun fromCountryLangList(value: List<String>): String {
        val jsonAdapter: JsonAdapter<List<String>> = moshi.adapter<List<String>>(type)
        return jsonAdapter.toJson(value)
    }

    @TypeConverter
    fun toCountryLangList(value: String): List<String> {
        val jsonAdapter: JsonAdapter<List<String>> = moshi.adapter<List<String>>(type)
        return jsonAdapter.fromJson(value) ?: emptyList()
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





