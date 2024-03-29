package stas.batura.radiotproject.ui.podcasts

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import stas.batura.data.ListViewType
import stas.batura.di.ServiceLocator
import stas.batura.repository.IPodcastRepository
import stas.batura.data.Podcast
import timber.log.Timber

class PodcastListViewModel (): ViewModel() {

    private val TAG = PodcastListViewModel::class.java.simpleName

    private val repository: IPodcastRepository = ServiceLocator.providePodcastRepository()

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }

    private val _spinner = MutableLiveData<Boolean>(false)
    val spinner: LiveData<Boolean>
        get() = _spinner

    val text: LiveData<String> = _text

    val activeNumPref = repository.getPrefActivePodcastNum().asLiveData()

    val combinePodcastState: LiveData<PodcastsListState> =
        repository.podcastViewType.flatMapLatest { viewType ->
            if (viewType != ListViewType.FAVORITE) {
                repository.getAllPodcastsList().combine(repository.getPrefActivePodcastNum()) { list, actNum ->
                    setActivePodcastState(list, actNum, viewType)
                }
            } else {
                repository.favTypeList().map {
                    PodcastsListState(it, viewType)
                }
            }
        }.asLiveData()

    val podcastTypeAndNumb = repository.getTypeAndNumb().asLiveData()

    init {
        launchDataLoad {
            repository.tryUpdateRecentRadioCache()
        }
    }

    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            try {
                _spinner.postValue(true)
                block()
            } catch (error: Throwable) {
                Timber.e("launchDataLoad: " + error)
            } finally {
                _spinner.postValue(false)
                repository.updateLastPodcPrefsNumber()
            }
        }
    }

    /**
     * указываем выводить ли детали для текущеего подкаста
     * @param podcast изменяемый подкаст
     * @param enabled статус изменения
     */
    fun onEnabled(podcast: Podcast, enabled: Boolean) {
        repository.updateTrackIdDetailed(podcast.podcastId, enabled)
    }

    fun addMorePodcasts() {
        launchDataLoad {
            repository.addMorePodcasts()
            ServiceLocator.provideAnalitic().addOldPodcast()
        }
    }

    fun getNextNPodcasts() {
//        repository.setPrefLastPtime(pod)
    }

//    /**
//     * сохраняем сколько выводить на экран
//     * @param num число выводимых на экран подкастов
//     */
//    fun changeNextListByNum(num: Int) {
//        viewModelScope.launch {
//            repository.changeLastPnumberByValue(num)
//        }
//    }

    /**
     * отмечаем помещать ли подкаст в избранное
     * @param podcastId номер подкаста
     * @param status если True то в избранном иначе нет
     */
    fun changeFavoritePodcastStatus(podcastId: Int, status: Boolean) {
        repository.setFavoriteStatus(podcastId, status)
    }

//    /**
//     * отмечаем что подкаст сохранен
//     * @param podcastId номер подкаста
//     */
//    fun changePodcastToSavedStatus(podcastId: Int) {
//        repository.updatePodcastSavedStatus(podcastId, SavedStatus.SAVED)
//    }
//
//    /**
//     * отмечаем что подкаст сохранен
//     * @param podcastId номер подкаста
//     */
//    fun changePodcastToNotSavedStatus(podcastId: Int) {
//        repository.updatePodcastSavedStatus(podcastId, SavedStatus.NOT_SAVED)
//    }
//
//    /**
//     * отмечаем что подкаст сохранен
//     * @param podcastId номер подкаста
//     */
//    fun changePodcastToLoadStatus(podcastId: Int) {
//        repository.updatePodcastSavedStatus(podcastId, SavedStatus.LOADING)
//    }
}

fun setActivePodcastState(podcasts: List<Podcast>, activeNum : Int, viewType: ListViewType): PodcastsListState {
    val timeS: Long = System.currentTimeMillis()
    val outList = mutableListOf<Podcast>()
    for (podcast in podcasts) {
        val podcastcopy = podcast.copy()
        if(podcast.podcastId == activeNum) {
            podcastcopy.isActive = true
        } else {
            podcastcopy.isActive = false
        }
        outList.add(podcastcopy)
    }
    val dur = System.currentTimeMillis() - timeS

    return PodcastsListState(outList, viewType)
}