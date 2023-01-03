package stas.batura.radiotproject.ui.podcasts

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import stas.batura.di.ServiceLocator
import stas.batura.radioproject.data.IRepository
import stas.batura.room.podcast.Podcast
import stas.batura.room.podcast.SavedStatus

class PodcastListViewModel (): ViewModel() {

    private val TAG = PodcastListViewModel::class.java.simpleName

    private val repository: IRepository = ServiceLocator.provideRepository()

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }

    private val _spinner = MutableLiveData<Boolean>(false)
    /**
     * Show a loading spinner if true
     */
    val spinner: LiveData<Boolean>
        get() = _spinner

    val text: LiveData<String> = _text

    val activeNumPref = repository.getPrefActivePodcastNum().asLiveData()

    // получаем список в зависимости от типа отображения
//    val newPodcastList: LiveData<List<Podcast>> = repository.getTypeAndNumb().
//        flatMapLatest { loadInfo ->
//            if (loadInfo.listType == ListViewType.YEAR) {
//                repository.yearTypeList()
//            } else if(loadInfo.listType == ListViewType.NUMBER){
//                repository.numberTypeList(loadInfo.lastNumb)
//            } else {
//                repository.favTypeList()
//            }
//        }.asLiveData()

//    val newPodcastList: LiveData<List<Podcast>> = repository.getAllPodcastsList().asLiveData()

    val newPodcastList: LiveData<List<Podcast>> =
        repository.getAllPodcastsList().combine(repository.getPrefActivePodcastNum()) { list, actNum ->

            val timeS: Long = System.currentTimeMillis()
            for (podcast in list) {
                if(podcast.podcastId == actNum) {
                    podcast.isActive = true
                } else {
                    podcast.isActive = false
                }
            }
            val dur = System.currentTimeMillis() - timeS
            Log.d(TAG, "time: $dur")

            list
    }.asLiveData()

    val podcastTypeAndNumb = repository.getTypeAndNumb().asLiveData()

    init {
        launchDataLoad {
            repository.tryUpdateRecentRadioCache()
        }
    }

    /**
     * Helper function to call a data load function with a loading spinner; errors will trigger a
     * snackbar.
     *
     * By marking [block] as [suspend] this creates a suspend lambda which can call suspend
     * functions.
     *
     * @param block lambda to actually load data. It is called in the viewModelScope. Before calling
     *              the lambda, the loading spinner will display. After completion or error, the
     *              loading spinner will stop.
     */
    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                _spinner.value = true
                block()
            } catch (error: Throwable) {
                Log.d(TAG, "launchDataLoad: " + error)
//                _snackbar.value = error.message
            } finally {
                _spinner.value = false
                repository.updateLastPodcPrefsNumber()
//                repository.getAllPodcastListFlow()
            }
        }
    }
    
    fun onDetailCheckClick(boolean: Boolean) {
        Log.d(TAG, "onDetailCheckClick: $boolean")
    }

    /**
     * указываем выводить ли детали для текущеего подкаста
     * @param podcast изменяемый подкаст
     * @param enabled статус изменения
     */
    fun onEnabled(podcast: Podcast, enabled: Boolean) {
        Log.d(TAG, "onEnabled: ")
        repository.updateTrackIdDetailed(podcast.podcastId, enabled)
    }

    fun addMorePodcasts() {
        launchDataLoad {
            repository.addMorePodcasts()
        }
    }

    fun getNextNPodcasts() {
//        repository.setPrefLastPtime(pod)
    }

    /**
     * сохраняем сколько выводить на экран
     * @param num число выводимых на экран подкастов
     */
    fun changeNextListByNum(num: Int) {
        viewModelScope.launch {
            repository.changeLastPnumberByValue(num)
        }
    }

    /**
     * отмечаем помещать ли подкаст в избранное
     * @param podcastId номер подкаста
     * @param status если True то в избранном иначе нет
     */
    fun changeFavoritePodcastStatus(podcastId: Int, status: Boolean) {
        repository.setFavoriteStatus(podcastId, status)
    }

    /**
     * отмечаем что подкаст сохранен
     * @param podcastId номер подкаста
     */
    fun changePodcastToSavedStatus(podcastId: Int) {
        repository.updatePodcastSavedStatus(podcastId, SavedStatus.SAVED)
    }

    /**
     * отмечаем что подкаст сохранен
     * @param podcastId номер подкаста
     */
    fun changePodcastToNotSavedStatus(podcastId: Int) {
        repository.updatePodcastSavedStatus(podcastId, SavedStatus.NOT_SAVED)
    }

    /**
     * отмечаем что подкаст сохранен
     * @param podcastId номер подкаста
     */
    fun changePodcastToLoadStatus(podcastId: Int) {
        repository.updatePodcastSavedStatus(podcastId, SavedStatus.LOADING)
    }
}