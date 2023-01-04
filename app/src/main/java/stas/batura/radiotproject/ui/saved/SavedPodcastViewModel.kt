package stas.batura.radiotproject.ui.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import stas.batura.data.SavedPodcast
import stas.batura.di.ServiceLocator
import stas.batura.repository.IRepository

class SavedPodcastViewModel: ViewModel() {

    private val repository: IRepository = ServiceLocator.provideRepository()

    val savedList = repository.savedPodcasts.asLiveData()

    fun deleteSavedPodcast(savedPodcast: SavedPodcast) {
        repository.deletePodcastCahe(savedPodcast.podcastId)
    }
}