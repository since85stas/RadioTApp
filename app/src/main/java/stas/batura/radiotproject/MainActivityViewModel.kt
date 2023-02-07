package stas.batura.radiotproject

import android.content.ServiceConnection
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.*
import com.google.android.exoplayer2.ExoPlayer
import stas.batura.data.ListViewType
import stas.batura.di.ServiceLocator
import stas.batura.repository.IPodcastRepository
import stas.batura.data.Podcast
import stas.batura.data.SavedStatus

class MainActivityViewModel constructor(

) : ViewModel() {

    private val TAG = MainActivityViewModel::class.java.simpleName

    private val repository: IPodcastRepository = ServiceLocator.providePodcastRepository()

    // checking connection
    val serviceConnection: MutableLiveData<ServiceConnection?> = MutableLiveData(null)

    val exoPlayer: LiveData<ExoPlayer> = RadioApp.ServiceHelper.exoPlayer

    val callbackChanges: LiveData<PlaybackStateCompat?> = RadioApp.ServiceHelper.callbackChanges

    private var _downloadPodcastEvent: MutableLiveData<Podcast?> = MutableLiveData(null)
    val downloadPodcastEvent: LiveData<Podcast?>
        get() = _downloadPodcastEvent

//    val activePodcastPref: MutableLiveData<Podcast?> = MutableLiveData(null)

//    // Create a Coroutine scope using a job to be able to cancel when needed
//    private var viewModelJob = Job()
//
//    // the Coroutine runs using the Main (UI) dispatcher
//    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val playClicked = MutableLiveData<Boolean?> (null)

    var _spinnerPlay: MutableLiveData<Boolean> = MutableLiveData(false)
    val spinnerPlay: LiveData<Boolean>
        get() = _spinnerPlay

    init {
        Log.d(TAG, "view model created: ")
    }

    /**
     * омечаем что включаетсЯ анимация проигрывания
     */
    fun playAnimVisible() {
        _spinnerPlay.value = true
    }

    /**
     * омечаем что выключаетсЯ анимация проигрывания
     */
    fun playAnimNotVisible() {
        _spinnerPlay.value = false
    }

    /**
     * нажали плей
     */
    fun playClicked() {
        if (RadioApp.ServiceHelper.mediaController != null) {

            RadioApp.ServiceHelper.mediaController?.transportControls?.play()
        }
    }

    /**
     * нажали паузу
     */
    fun pauseClicked() {
//        if (mediaController.value != null) {
        RadioApp.ServiceHelper.mediaController?.transportControls?.pause()
//        }
    }

    /**
     * изменяем состояние кнопки
     */
    fun changePlayState() {
        if ( callbackChanges.value != null) {
            if (callbackChanges.value!!.state == PlaybackStateCompat.STATE_PLAYING) {
                RadioApp.ServiceHelper.mediaController?.transportControls?.pause()
            } else {
                RadioApp.ServiceHelper.mediaController?.transportControls?.play()
            }
        }
    }

    /**
     * начать проигрывание подкаста с заданного времени
     * @param position: позиция с которой начинаем проигрывать в мс от начала
     * @param podcast: подкаст который проигрываем
     */
    fun movingPlayToPosition(position: Long, podcast: Podcast) {

        playClicked.postValue(true)

        // если уже играет то остонавливаем
        if (callbackChanges.value != null && callbackChanges.value!!.state.equals(
                PlaybackStateCompat.STATE_PLAYING
            )
        ) {
            RadioApp.ServiceHelper.mediaController?.transportControls?.stop()
        }

        // указываем, какой номер теперь активный
        setActiveNumberPref(podcast.podcastId)

        // посылаем в сервис и проигрываем
        RadioApp.ServiceHelper.serviceBinder?.setPodcastWithPosition(podcast, position)
        playClicked()

        if (podcast != ServiceLocator.provideOnlinePodcastLink()) {
            ServiceLocator.provideAnalitic().playEvent()
        } else {
            ServiceLocator.provideAnalitic().onlineEvent()
        }
    }

    /**
     * устанавливаем тип вывода списка
     * @param type тип вывода
     */
    fun setPrefsListType(type: ListViewType) {
        repository.setPrefListType(type)
    }

    fun fastForward() {
        RadioApp.serviceBinder?.moveForward()
    }

    fun fastRewind() {
        RadioApp.serviceBinder?.moveBack()
    }

//    /**
//     * устанавливаем кол-во на странице
//     * @param num: число подкастов выводимых на экран
//     */
//    fun updatePrefPodcastNum(num: Int) {
////        repository.setPrefListType(ListViewType.NUMBER)
//        repository.setNumPodcsts(num)
//    }
//
//    /**
//     * устанавливаем и Год
//     * @param year: год за который выводим список
//     */
//    fun getPodcasttsInYear(year: Year) {
////        repository.setPrefListType(ListViewType.YEAR)
//        repository.setPrefSelectedYear(year)
//    }
//
//    // пока не используется
//    fun setCheckBoxInitState(boolean: Boolean) {
//        smallCheck.value = boolean
//    }

    /**
     * сохраняем номер активного подкаста
     * @param number: номер выбранного подкаста
     */
    fun setActiveNumberPref(number: Int) {
        repository.setPrefActivePodcastNum(number)
    }


    fun startDownloadPodcast(podcast: Podcast) {

        // обновляем статус о загрузке
        repository.updatePodcastSavedStatus(podcastId = podcast.podcastId, savedStatus = SavedStatus.LOADING)

        ServiceLocator.provideAnalitic().downloadEvent()

        _downloadPodcastEvent.value = podcast
    }

    fun endDownloadPodcast(podcastId: Int, localPath: String) {
        repository.setPodcastToSaved(podcastId, localPath)
    }

//    /**
//     * фабрика для создания модели
//     */
//    class Factory(
//        private val repository: IRepository,
//        private val application: Application
//    ) : ViewModelProvider.Factory {
//        override fun <T : ViewModel> create(modelClass: Class<T>): T {
//            if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
//                return MainActivityViewModel(repository, application) as T
//            }
//            throw IllegalArgumentException("Unknown ViewModel class")
//        }
//
//    }

}