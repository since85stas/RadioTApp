package stas.batura.radiotproject.ui.online

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import stas.batura.di.ServiceLocator
import stas.batura.room.podcast.Podcast

class OnlineViewModel: ViewModel() {

    // экземпляр для ссылки на онлайн трансляцию, может надо куда-то вынести, но пока тут
    val ONLINE_PODCAST = Podcast(
        podcastId = -1,
        title = "Эфир",
        audioUrl = "https://stream.radio-t.com/"
    )

    val timerValues = ServiceLocator.provideTimerValues().asLiveData()


}