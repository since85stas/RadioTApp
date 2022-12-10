package stas.batura.room.podcast

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import stas.batura.data.PodcastBody
import stas.batura.data.fillTimelable

import stas.batura.retrofit.TimeLabel
import stas.batura.utils.TIME_WEEK
import stas.batura.utils.getLinksFromHtml
import stas.batura.utils.getMillisTime
import java.io.IOException
import java.lang.NumberFormatException
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

            var podcastNum: Int

            try {
                podcastNum = num.toInt()
            } catch (e: NumberFormatException) {
                Log.d("CreatePodcast", "error: $e in $podcastBody")
                podcastNum = 0
            }

            return Podcast(
                podcastNum,
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

    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val type = Types.newParameterizedType(List::class.java, String::class.java)

    @TypeConverter()
    fun fromCountryLangList(value: List<String>?): String {
        if (value != null) {
            val jsonAdapter: JsonAdapter<List<String>> = moshi.adapter<List<String>>(type)
            return jsonAdapter.toJson(value)
        } else {
            return ""
        }
    }

    @TypeConverter
    fun toCountryLangList(value: String): List<String> {
        val jsonAdapter: JsonAdapter<List<String>> = moshi.adapter<List<String>>(type)
        try {
            return jsonAdapter.fromJson(value) ?: emptyList()
        } catch (e: IOException) {
            println(e.toString())
            return emptyList()
        }

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





