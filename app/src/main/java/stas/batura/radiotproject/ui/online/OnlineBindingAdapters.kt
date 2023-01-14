package stas.batura.radiotproject.ui.online

import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import stas.batura.retrofit.TimeLabel

// считаем что подкаст идет примерно 2 часа, в это время показываем кнопку с возможностью прослушивания
// поэтому время неделя минус 2 часа в секундах
const val WEEK_WITHOUT_2HOURS_SEC = (3600*24*7-3600*2)

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
            "${days} д:$hoursStr:$minutesStr:$secStr"
        } else if (hours > 0) {
        "$hoursStr:$minutesStr:$secStr"
    } else if (minutes > 0) {
        "00:$minutesStr:$secStr"
    } else {
        "00:00:$secStr"
    }
    setText(timeString)
}

@BindingAdapter("timerTextVisibility")
fun TextView.timerTextVisibility(time: Long) {
    if (time > 0 && time > WEEK_WITHOUT_2HOURS_SEC) {
        visibility = View.INVISIBLE
    } else {
        visibility = View.VISIBLE
    }
}

@BindingAdapter("onlineButtonVisibility")
fun TextView.onlineButtonVisibility(time: Long) {
    if (time == 0L || time > WEEK_WITHOUT_2HOURS_SEC) {
        visibility = View.VISIBLE
    } else {
        visibility = View.INVISIBLE
    }
}

//@BindingAdapter("telegramChatBind")
//fun TextView.telegramChatBind() {
//    val spannableString = SpannableString(text)
//    spannableString.setSpan(
//        UnderlineSpan(),
//        0,
//        text.length,
//        Spannable.SPAN_INCLUSIVE_INCLUSIVE
//    )
//    text = spannableString
//}