package stas.batura.musicproject.ui.control

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.map
import stas.batura.di.ServiceLocator

class ControlViewModel () : ViewModel () {

    val repository = ServiceLocator.providePodcastRepository()

    val activePodcast = repository.getPrefActivePodcastNum().map { podcastNum ->
        val podcast = repository.getActivePodcastSus(podcastNum)
        return@map podcast
    }.asLiveData()

}