package stas.batura.radiotproject

import android.animation.ObjectAnimator
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerControlView
import kotlinx.android.synthetic.main.control_fragment_new.view.*
import stas.batura.data.ListViewType
import stas.batura.data.SavedPodcast
import stas.batura.radioproject.data.PodcastLoadInfo
import stas.batura.retrofit.TimeLabel
import stas.batura.room.podcast.Podcast
import stas.batura.room.podcast.SavedStatus
import java.text.SimpleDateFormat

@BindingAdapter("titleBind")
fun TextView.podcastTitleBind(title: String) {
    text = "${title}: "
}

@BindingAdapter("podcastTime")
fun TextView.podactTime(time: Long) {
    text = "${createPodcastDateTitle(time)} "
}

@BindingAdapter("urlBind")
fun TextView.podcastUrlBind(podcast: Podcast) {
    text = podcast.url
}

@BindingAdapter("progressBarVisibility")
fun ProgressBar.bindVisibility(visible: Boolean) {
    if (visible) {
        visibility = View.VISIBLE
    } else {
        visibility = View.GONE
    }
}

@BindingAdapter("playProgressBarVisibility")
fun ProgressBar.playProgressBarVisibility(visible: Boolean) {
    visibility = if (visible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter("recyclerBarVisibility")
fun RecyclerView.bindVisibility(visible: Boolean) {
    if (visible) {
        visibility = View.VISIBLE
    } else {
        visibility = View.GONE
    }
}

@BindingAdapter("testVisibility")
fun ProgressBar.testVisibility(visible: Boolean) {
    Log.d("testVisibility", "testVisibility: $visible")
    if (visible) {
        visibility = View.VISIBLE
    } else {
        visibility = View.GONE
    }
}

//@BindingAdapter("currentPodcast")
//fun bindcurrentPodcast(podcast: Podcast) {
//    Log.d("bindcurrentPodcast", "bindcurrentPodcast: $podcast")
//}

@BindingAdapter(value = ["playProgressBarVisibility", "activePodcastId","itemPodcast"])
fun ProgressBar.bindplayPVisibility(visible: Boolean, podcastActiveId: Int?, itemPodcastId: Int) {
    if (visible) {
        visibility = View.VISIBLE
        Log.d("bindplayPVisibility", "$visible is visible: $podcastActiveId $itemPodcastId ")
        if (podcastActiveId != null) {
            if (itemPodcastId == podcastActiveId) {
//                Log.d("bindplayPVisibility", "is active: ")
                visibility = View.VISIBLE
            } else {
//                Log.d("bindplayPVisibility", "not active: ")
                visibility = View.GONE
            }
        }
    } else {
        visibility = View.GONE
//        Log.d("bindplayPVisibility", "$visible not visible: ")
    }
}

@BindingAdapter("timelableTimeBind")
fun TextView.timelableTimeBind(timeLabel: TimeLabel) {
    if (timeLabel.startTimeString!= null) {
        text = timeLabel.startTimeString
    }
}

@BindingAdapter("timelableTitleBind")
fun TextView.timelableTitleBind(timeLabel: TimeLabel) {
    val spannableString = SpannableString(timeLabel.topic)
    spannableString.setSpan(
        UnderlineSpan(),
        0,
        timeLabel.topic.length,
        Spannable.SPAN_INCLUSIVE_INCLUSIVE
    )
    text = spannableString
}

@BindingAdapter("bindExoPla")
fun PlayerControlView.bindPlayer(exoPlayer: ExoPlayer?) {
    if (exoPlayer != null) {
        player = exoPlayer
    }
}

@BindingAdapter("controlTitleBind")
fun TextView.controlTitleBind(podcast: Podcast?) {
    text = podcast?.title
}

@BindingAdapter("combineTitleBind")
fun TextView.combineTitleBind(string: String?) {
    if (string != null) {
        text = string
    }
}

@BindingAdapter("bindProgress")
fun ProgressBar.bindProgress(podcast: Podcast) {
    progress = podcast.getPlayedInPercent()
}

@BindingAdapter("bindPodcastHeaderTitle")
fun TextView.bindPodcastHeaderTitle(podcastInfo: PodcastLoadInfo) {
    when(podcastInfo.listType) {
        ListViewType.NUMBER -> text = "Выпуски:"
        ListViewType.YEAR -> text = "Год:"
        ListViewType.MONTH -> text = "Месяц:"
        else -> {}
    }
}

@BindingAdapter("bindPodcastHeaderNumbers")
fun TextView.bindPodcastHeaderNumbers(list: List<Podcast>?) {
    list?.let { list ->  if (!list.isEmpty()) text = "${list.first().podcastId}-${list.last().podcastId}"}
}


@BindingAdapter("favoriteEnebleVisibility")
fun ImageView.favoriteEnebleVisibility(podcast: Podcast) {
    if (podcast.isFavorite) {
        visibility = View.VISIBLE
    } else {
        visibility = View.INVISIBLE
    }
}

@BindingAdapter("favoriteDisableVisibility")
fun ImageView.favoriteDisableVisibility(podcast: Podcast) {
    if (!podcast.isFavorite) {
        visibility = View.VISIBLE
    } else {
        visibility = View.INVISIBLE
    }
}

@BindingAdapter("downloadedVisibility")
fun ImageView.downloadedVisibility(podcast: Podcast) {
    if (podcast.savedStatus == SavedStatus.NOT_SAVED || podcast.savedStatus == SavedStatus.LOADING) {
        visibility = View.VISIBLE
    } else {
        visibility = View.INVISIBLE
    }
}

@BindingAdapter("deleteVisibility")
fun ImageView.deleteVisibility(podcast: Podcast) {
    if (podcast.savedStatus == SavedStatus.SAVED) {
        visibility = View.VISIBLE
    } else {
        visibility = View.INVISIBLE
    }
}

@BindingAdapter("themesVisibility")
fun CheckBox.themesVisibility(podcast: Podcast) {
    if (podcast.timeLabels != null && !podcast.timeLabels!!.isEmpty()) {
        visibility = View.VISIBLE
    } else {
        visibility = View.INVISIBLE
    }
}

//@BindingAdapter("applyAnimation")
//fun ImageView.applyAnimation(podcast: Podcast) {
//    if (podcast.savedStatus == SavedStatus.LOADING) {
//        val animator = ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0.2f)
//        animator.duration = 1300
//        animator.repeatCount = ObjectAnimator.INFINITE
//        animator.repeatMode = ObjectAnimator.REVERSE
//        animator.start()
//    }
//}
