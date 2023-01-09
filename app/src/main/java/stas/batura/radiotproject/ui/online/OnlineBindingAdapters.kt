package stas.batura.radiotproject.ui.online

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("timerTextBind")
fun TextView.timerTextBind(time: Long) {
    var timeDel = time
    val days = (time /(3600*24))

    timeDel = timeDel - days*(3600*24)
    val hours = (timeDel / 3600).toInt()
    val hoursStr = if (hours < 10) {
        "0$hours"
    } else {
        "$hours"
    }
    timeDel = timeDel - hours*3600
    val minutes = ((timeDel) / 60).toInt()
    val minutesStr = if (minutes < 10) {
        "0$minutes"
    } else {
        "$minutes"
    }
    timeDel = timeDel - minutes*60
    val sec: Int = (timeDel).toInt()
    val secStr = if (sec < 10) {
        "0$sec"
    } else {
        "$sec"
    }
    val timeString: String = if (days >0) {
            "${days} дня:$hoursStr:$minutesStr:$secStr"
        } else if (hours > 0) {
        "$hoursStr:$minutesStr:$secStr"
    } else if (minutes > 0) {
        "00:$minutesStr:$secStr"
    } else {
        "00:00:$secStr"
    }
    setText(timeString)
}