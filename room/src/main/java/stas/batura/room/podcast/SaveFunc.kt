package stas.batura.room.podcast

import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import stas.batura.data.TimeLabel
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class TimeLabelsDataConverter {

    @TypeConverter()
    fun fromTimeLableList(value: List<TimeLabel>?): String {
        if (value != null) {
            val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val type = Types.newParameterizedType(List::class.java, TimeLabel::class.java)
            val jsonAdapter: JsonAdapter<List<TimeLabel>> = moshi.adapter<List<TimeLabel>>(type)
            return jsonAdapter.toJson(value)
        } else {
            return ""
        }
    }

    @TypeConverter
    fun toTimeLableList(value: String): List<TimeLabel> {
        val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val type = Types.newParameterizedType(List::class.java, TimeLabel::class.java)
        val jsonAdapter: JsonAdapter<List<TimeLabel>> = moshi.adapter<List<TimeLabel>>(type)

        try {
            val out = jsonAdapter.fromJson(value)
            return out ?: emptyList()
        } catch (e: IOException) {
            return emptyList()
        }
    }

}


fun setTrackDurat (dur: Long): String {

    val formatter = SimpleDateFormat("HH:mm:ss", Locale.GERMAN);
    val dateString = formatter.format( dur );
   return dateString
}

