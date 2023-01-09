package stas.batura.radiotproject.ui.online

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("timerTextBind")
fun TextView.timerTextBind(time: Long) {
    var timeDel = time
    val days = (time /(3600*24))
    timeDel = timeDel - days*(3600*24)
    val hours = (timeDel / 3600).toInt()
    timeDel = timeDel - hours*3600
    val minutes = ((timeDel) / 60).toInt()
    timeDel = timeDel - minutes*60
    val sec: Int = (timeDel).toInt()
    val timeString: String = if (days >0) {
            "$days:$hours:$minutes:$sec"
        } else if (hours > 0) {
        "$hours:$minutes:$sec"
    } else if (minutes > 0) {
        "00:$minutes:$sec"
    } else {
        "00:00:$sec"
    }
    setText("testtttttttttttttttt")
}