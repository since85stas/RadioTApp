package stas.batura.radiotproject

import android.app.Application
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.*
import com.google.android.exoplayer2.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import stas.batura.data.ListViewType
import stas.batura.data.Year
import stas.batura.di.ServiceLocator
import stas.batura.radiotproject.player.MusicService
import stas.batura.radioproject.data.IRepository
import stas.batura.room.podcast.Podcast

class MainActivityViewModel constructor(

) : ViewModel() {

    private val TAG = MainActivityViewModel::class.java.simpleName

//    private val application = ServiceLocator.provideContext()

    private val repository: IRepository = ServiceLocator.provideRepository()

    // binder instance
    var playerServiceBinder: MusicService.PlayerServiceBinder? = null

    // media controller for interaction
    val mediaController: MutableLiveData<MediaControllerCompat?> = MutableLiveData()

    // getting changes from service
    private var callback: MediaControllerCompat.Callback? = null

    // checking connection
    val serviceConnection: MutableLiveData<ServiceConnection?> = MutableLiveData(null)

    val exoPlayer: MutableLiveData<ExoPlayer> = MutableLiveData()

    val callbackChanges: MutableLiveData<PlaybackStateCompat?> = MutableLiveData(null)

    private var _createServiceListner: MutableLiveData<Boolean> = MutableLiveData(false)
    val createServiceListner: LiveData<Boolean>
        get() = _createServiceListner

    private var _downloadPodcastEvent: MutableLiveData<Podcast?> = MutableLiveData(null)
    val downloadPodcastEvent: LiveData<Podcast?>
        get() = _downloadPodcastEvent

    val activePodcastPref: MutableLiveData<Podcast?> = MutableLiveData(null)

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val smallCheck = MutableLiveData<Boolean?> (null)

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
     * создает музыкальный сервис и его контроллер
     */
    fun initMusicService() {
        if (serviceConnection.value == null) {
            // привязываем колбека и лайв дэйта
            callback = object : MediaControllerCompat.Callback() {
                override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                    callbackChanges.value = state
                }
            }

            // соединение с сервисом
            serviceConnection.value = object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName, service: IBinder) {
                    playerServiceBinder = service as MusicService.PlayerServiceBinder
                    try {
                        mediaController.value = MediaControllerCompat(
                            ServiceLocator.provideContext(),
                            playerServiceBinder!!.getMediaSessionToke()
                        )

                        exoPlayer.value = playerServiceBinder!!.getPlayer()

                        mediaController.value!!.registerCallback(callback!!)
                        callback!!.onPlaybackStateChanged(mediaController.value!!.playbackState)
                    } catch (e: RemoteException) {
                        mediaController.value = null
                    }
                }

                override fun onServiceDisconnected(name: ComponentName) {
                    playerServiceBinder = null
                    if (mediaController.value != null) {
                        mediaController.value!!.unregisterCallback(callback!!)
                        mediaController.value = null
                    }
                }
            }
        }
    }

    /**
     * нажали плей
     */
    fun playClicked() {
        if (mediaController.value != null) {
            mediaController.value!!.transportControls.play()
        }
    }

    /**
     * нажали паузу
     */
    fun pauseClicked() {
        if (mediaController.value != null) {
            mediaController.value!!.transportControls.pause()
        }
    }

    /**
     * изменяем состояние кнопки
     */
    fun changePlayState() {
        if (mediaController.value != null && callbackChanges.value != null) {
            if (callbackChanges.value!!.state == PlaybackStateCompat.STATE_PLAYING) {
                mediaController.value!!.transportControls.pause()
            } else {
                mediaController.value!!.transportControls.play()
            }
        }
    }

    /**
     * начать проигрывание подкаста с заданного времени
     * @param position: позиция с которой начинаем проигрывать в мс от начала
     * @param podcast: подкаст который проигрываем
     */
    fun movingPlayToPosition(position: Long, podcast: Podcast) {
        // если уже играет то остонавливаем
        if (callbackChanges.value != null && callbackChanges.value!!.state.equals(
                PlaybackStateCompat.STATE_PLAYING
            )
        ) {
            mediaController.value!!.transportControls.stop()
        }

        // указываем, какой номер теперь активный
        setActiveNumberPref(podcast.podcastId)

        // посылаем в сервис и проигрываем
        playerServiceBinder!!.setPodcastWithPosition(podcast, position)
        playClicked()
    }

    /**
     * устанавливаем тип вывода списка
     * @param type тип вывода
     */
    fun setPrefsListType(type: ListViewType) {
        repository.setPrefListType(type)
    }

    /**
     * устанавливаем кол-во на странице
     * @param num: число подкастов выводимых на экран
     */
    fun updatePrefPodcastNum(num: Int) {
//        repository.setPrefListType(ListViewType.NUMBER)
        repository.setNumPodcsts(num)
    }

    /**
     * устанавливаем и Год
     * @param year: год за который выводим список
     */
    fun getPodcasttsInYear(year: Year) {
//        repository.setPrefListType(ListViewType.YEAR)
        repository.setPrefSelectedYear(year)
    }

    // пока не используется
    fun setCheckBoxInitState(boolean: Boolean) {
        smallCheck.value = boolean
    }

    /**
     * сохраняем номер активного подкаста
     * @param number: номер выбранного подкаста
     */
    fun setActiveNumberPref(number: Int) {
        repository.setPrefActivePodcastNum(number)
    }

    /**
     * получаем активный подкаст в ViewModel
     * @param num: номер выбранного подкаста
     */
    fun updateActivePodcast(num: Int) {
        viewModelScope.launch {
            val podcast = repository.getActivePodcastSus(num)
            activePodcastPref.value = podcast
        }
    }

    // TODO: подумать как изменить
    /**
     * вспомог функция, для принуд перерисовки одной строки в списке
     */
    fun redrawItemById() {
        if (activePodcastPref.value != null) {
            viewModelScope.launch {
                repository.updateRedrawField(activePodcastPref.value!!.podcastId)
            }
        }
    }

    fun startDownloadPodcast(podcast: Podcast) {
        _downloadPodcastEvent.value = podcast
    }

    fun endDownloadPodcast(podcastId: Int, localPath: String) {

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