package stas.batura.radiotproject.ui.online

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("timerTextBind")
fun TextView.timerTextBind(time: Long) {
    val days = (time /(3600*24))
    val hours = (time / 3600).toInt()
    val minutes = ((time - hours * 3600) / 60).toInt()
    val sec: Int = (time - minutes * 60 - hours * 3600).toInt()
    val timeString: String
    timeString =
        if (days >0) {
            "$days:$hours:$minutes:$sec"
        } else if (hours > 0) {
        "$hours:$minutes:$sec"
    } else if (minutes > 0) {
        "00:$minutes:$sec"
    } else {
        "00:00:$sec"
    }
    text = timeString
}