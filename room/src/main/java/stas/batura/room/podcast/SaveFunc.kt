package stas.batura.room.podcast

import android.widget.TextView
import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import stas.batura.retrofit.TimeLabel
import java.lang.StringBuilder
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern



class TimeLabelsDataConverter {

    @TypeConverter()
    fun fromTimeLableList(value: List<TimeLabel>): String {
        val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val type = Types.newParameterizedType(List::class.java, TimeLabel::class.java)
        val jsonAdapter: JsonAdapter<List<TimeLabel>> = moshi.adapter<List<TimeLabel>>(type)
        return jsonAdapter.toJson(value)
    }

    @TypeConverter
    fun toTimeLableList(value: String): List<TimeLabel> {
        val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val type = Types.newParameterizedType(List::class.java, TimeLabel::class.java)
        val jsonAdapter: JsonAdapter<List<TimeLabel>> = moshi.adapter<List<TimeLabel>>(type)
        val out = jsonAdapter.fromJson(value)
        return out ?: emptyList()
    }

}


fun setTrackDurat (dur: Long): String {

    val formatter = SimpleDateFormat("HH:mm:ss", Locale.GERMAN);
    val dateString = formatter.format( dur );
   return dateString
}

