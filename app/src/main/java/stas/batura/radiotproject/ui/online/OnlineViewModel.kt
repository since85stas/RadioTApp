package stas.batura.radiotproject.ui.online

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import stas.batura.di.ServiceLocator
import stas.batura.room.podcast.Podcast

class OnlineViewModel: ViewModel() {

    val ONLINE_PODCAST = Podcast(
        podcastId = -1,
        title = "Эфир",
        audioUrl = "http://cdn.radio-t.com/rt_podcast.mp3"
//    audioUrl = "https://stream.radio-t.com/"
    )

    val timerValues = ServiceLocator.provideTimerValues().asLiveData()


}