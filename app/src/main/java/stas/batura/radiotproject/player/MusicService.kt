package stas.batura.radiotproject.player

import android.app.*
import android.content.*
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.media.session.MediaButtonReceiver
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.upstream.cache.*
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import stas.batura.data.Podcast
import stas.batura.data.SavedStatus
import stas.batura.di.ServiceLocator
import stas.batura.radiotproject.MainActivity
import stas.batura.radiotproject.R
import stas.batura.repository.IPodcastRepository
import timber.log.Timber


class MusicService() : LifecycleService() {

    var repositoryS: IPodcastRepository = ServiceLocator.providePodcastRepository()

    var mediaSession: MediaSessionCompat? = null
//        MediaSessionCompat(ServiceLocator.provideContext(), "Music Service")

    val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()

    private val TAG = MusicService::class.java.simpleName

    private val NOTIF_CHANNEL_NAME = "audio.stas.chanel"

    private val NOTIFICATION_ID = 404

    private val NOTIFICATION_DEFAULT_CHANNEL_ID = "default_channel"

    private var podcast: Podcast? = null

    var playbackPosition: Long = 0

    // билдер для данных
    private val metadataBuilder = MediaMetadataCompat.Builder()

    // плэйбэк
    private val stateBuilder: PlaybackStateCompat.Builder =
        PlaybackStateCompat.Builder().setActions(
            PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_STOP
                    or PlaybackStateCompat.ACTION_PAUSE
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE
        )

//    private var mediaSession: MediaSessionCompat? = null

    private var audioManager: AudioManager? = null

    private var audioFocusRequest: AudioFocusRequest? = null

    private var isAudioFocusRequested = false

    var exoPlayer: SimpleExoPlayer? = null
//    private var extractorsFactory: ExtractorsFactory? = null

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var serviceJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(serviceJob + Dispatchers.IO)

    init {
        Log.d(TAG, "init service: ")
    }

    fun provideDatasourceFactory(): DataSource.Factory {
        val httpDataSourceFactory: DataSource.Factory =
            OkHttpDataSourceFactory(
                OkHttpClient(),
                Util.getUserAgent(
                    ServiceLocator.provideContext(),
                    "radiotapp"
                )
            )


        val dataSourceFactory = CacheDataSourceFactory(
            ServiceLocator.provideExoCache(),
            httpDataSourceFactory,
            CacheDataSource.FLAG_BLOCK_ON_CACHE or CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
        )

        return dataSourceFactory
    }

    fun provideLocalDatasourceFactory(): DataSource.Factory {
        val fileDataSource = FileDataSource()

        val factory =
            DataSource.Factory { fileDataSource }

        val dataSourceFactory = CacheDataSourceFactory(
            ServiceLocator.provideExoCache(),
            factory,
            CacheDataSource.FLAG_BLOCK_ON_CACHE or CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
        )

        return dataSourceFactory
    }

    override fun onCreate() {
        super.onCreate()

        // создаем аудио менеджер
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // настраиваем уведомления
           val notificationChannel =
                NotificationChannel(
                    NOTIFICATION_DEFAULT_CHANNEL_ID,
                    NOTIF_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
            val audioAttributes =
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()

            // запрос на аудио фокус
            audioFocusRequest =
                AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setOnAudioFocusChangeListener(audioFocusChangeListener)
                    .setAcceptsDelayedFocusGain(false)
                    .setWillPauseWhenDucked(true)
                    .setAudioAttributes(audioAttributes)
                    .build()
        }

        val activityIntent = Intent(this, MainActivity::class.java)

        // создаем и настраиваем медиа сессию
        mediaSession = MediaSessionCompat(this,"Music Service", null, PendingIntent.getActivity(
            applicationContext,
            0,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        ))

        mediaSession?.apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            setCallback(mediaSessionCallback)

            val mediaButtonIntent = Intent(
                Intent.ACTION_MEDIA_BUTTON, null, applicationContext,
                MediaButtonReceiver::class.java
            )

            // настраиваем получатель кнопок
            setMediaButtonReceiver(
                PendingIntent.getBroadcast(
                    applicationContext,
                    0,
                    mediaButtonIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )

            setSessionActivity(
                PendingIntent.getActivity(
                    applicationContext,
                    0,
                    activityIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )


        }

        // создаем плеер
        exoPlayer = ExoPlayerFactory.newSimpleInstance(
            this,
            DefaultRenderersFactory(this),
            DefaultTrackSelector(),
            DefaultLoadControl()
        )

        // добавляем слушатель
        exoPlayer!!.addListener(exoPlayerListener)

        createInitNotification()

    }

    override fun unbindService(conn: ServiceConnection) {
        Timber.d("unbind service")
        super.unbindService(conn)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Timber.d("on unbind")
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        Timber.d("on reunbind")
        super.onRebind(intent)
    }

    /**
     * создаем колбэк для управления сервисом
     */
    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {


        private var currentUri: Uri? = null
        var currentState = PlaybackStateCompat.STATE_STOPPED

        // при подготовке сервиса
        override fun onPrepare() {
            super.onPrepare()
        }



        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
        }



        // при начале проигрыша
        override fun onPlay() {

            exoPlayer?.let {

                updateMetadataFromTrack(podcast!!)
                if (!isAudioFocusRequested) {
                    isAudioFocusRequested = true
                    var audioFocusResult: Int
                    audioFocusResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        audioManager!!.requestAudioFocus(audioFocusRequest!!)
                    } else {
                        audioManager!!.requestAudioFocus(
                            audioFocusChangeListener,
                            AudioManager.STREAM_MUSIC,
                            AudioManager.AUDIOFOCUS_GAIN
                        )
                    }
                    if (audioFocusResult != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) return
                }
                mediaSession?.isActive = true // Сразу после получения фокуса
                registerReceiver(
                    becomingNoisyReceiver,
                    IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
                )

                exoPlayer!!.playWhenReady = true

                // переводим в нужную точку
                mediaSession!!.setPlaybackState(
                    stateBuilder.setState(
                        PlaybackStateCompat.STATE_PLAYING,
                        PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                        1f
                    ).build()
                )
                currentState = PlaybackStateCompat.STATE_PLAYING

                refreshNotificationAndForegroundStatus(currentState)

                playTrack()
            }

            Log.d(TAG, "onPlay: prepeared $podcast")


        }

        // при остановки проигрыша
        override fun onPause() {
            playbackPosition = exoPlayer!!.currentPosition

            updateCurrePodcastPosit(playbackPosition)

            if (exoPlayer!!.playWhenReady) {
                exoPlayer!!.playWhenReady = false
                unregisterReceiver(becomingNoisyReceiver)
            }

            mediaSession!!.setPlaybackState(
                stateBuilder.setState(
                    PlaybackStateCompat.STATE_PAUSED,
                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                    1f
                ).build()
            )
            currentState = PlaybackStateCompat.STATE_PAUSED

            refreshNotificationAndForegroundStatus(currentState)
        }

        // при остановки проигрыша
        override fun onStop() {
            updateCurrePodcastPosit(playbackPosition)

            Log.d(TAG, "onStop: ")
            if (exoPlayer!!.playWhenReady) {
                exoPlayer!!.playWhenReady = false
                unregisterReceiver(becomingNoisyReceiver)
            }

            if (isAudioFocusRequested) {
                isAudioFocusRequested = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    audioManager!!.abandonAudioFocusRequest(audioFocusRequest!!)
                } else {
                    audioManager!!.abandonAudioFocus(audioFocusChangeListener)
                }
            }

            mediaSession!!.isActive = false

            mediaSession!!.setPlaybackState(
                stateBuilder.setState(
                    PlaybackStateCompat.STATE_STOPPED,
                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                    1f
                ).build()
            )
            currentState = PlaybackStateCompat.STATE_STOPPED

            refreshNotificationAndForegroundStatus(currentState)

            stopSelf()
        }


        // подготавливаем трэк
        fun setTrackPathToPlay(uri: Uri, islocal: Boolean) {

            currentUri = uri
            if (!islocal) {
                val mediaSource =
                    ExtractorMediaSource(
                        uri,
                        provideDatasourceFactory(),
                        extractorsFactory,
                        null,
                        null
                    )
                exoPlayer!!.setMediaSource(mediaSource)
            } else {
                val mediaSource =
                    ExtractorMediaSource(
                        uri,
                        provideLocalDatasourceFactory(),
                        extractorsFactory,
                        null,
                        null
                    )
                exoPlayer!!.setMediaSource(mediaSource)
            }

            exoPlayer!!.prepare()

            exoPlayer!!.seekTo(playbackPosition)
        }

        fun playTrack() {
            if (podcast?.savedStatus == SavedStatus.SAVED) {
                CoroutineScope(Dispatchers.Default).launch {
                    val localUri =
                        repositoryS.getPodcastLocalPath(podcastId = podcast!!.podcastId)

                    CoroutineScope(Dispatchers.Main).launch {
                        setTrackPathToPlay(Uri.parse(localUri), true)
                    }
                }
            } else {
                setTrackPathToPlay(Uri.parse(podcast!!.audioUrl), false)
            }
        }

        // обновляем данные о треке
        private fun updateMetadataFromTrack(podcast: Podcast) {

                CoroutineScope(Dispatchers.IO).launch {

                    if (podcast.imageUrl != null) {
                        val image = Glide.with(ServiceLocator.provideContext())
                            .asBitmap()
                            .load(podcast.imageUrl)
                            .into(100, 100)
                            .get();

                        metadataBuilder.putBitmap(
                            MediaMetadataCompat.METADATA_KEY_ART,
                            image
                        )
                    } else {
                        // добавить дефолтную картинку
                    }
                    metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, podcast.title)
                    mediaSession!!.setMetadata(metadataBuilder.build())
                }

        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession!!, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        println("Service bind")
        return PlayerServiceBinder()
    }

    // слушатель на плеер
    private val exoPlayerListener: Player.EventListener = object : Player.EventListener {
        override fun onTracksChanged(
            trackGroups: TrackGroupArray,
            trackSelections: TrackSelectionArray
        ) {

        }

        override fun onPlayerStateChanged(
            playWhenReady: Boolean,
            playbackState: Int
        ) {
            Log.d(TAG, "onPlayerStateChanged: $playbackState")
            if (playWhenReady && playbackState == ExoPlayer.STATE_ENDED) {
//                mediaSessionCallback.onSkipToNext()
            } else if (playbackState == ExoPlayer.STATE_READY) {
                val realDurationMillis = exoPlayer!!.duration

                // updating duration in DB
                if (podcast!!.durationInMillis == 0L) {
                    repositoryS.updateTrackDuration(podcast!!.podcastId, realDurationMillis)
                }

                Log.d(TAG, "onPlayerStateChanged: duration $realDurationMillis")
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            Timber.d("isPlay $isPlaying")
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            Timber.d(error.toString())
            Toast.makeText(this@MusicService, "Ошибка воспроизведения", Toast.LENGTH_LONG).show()
            ServiceLocator.provideAnalitic().errorPlayEvent()
            mediaSessionCallback.onPause()
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
        }
    }

    // отклик на фокус
    private val audioFocusChangeListener: OnAudioFocusChangeListener =
        OnAudioFocusChangeListener { focusChange: Int ->
            when (focusChange) {
                // Не очень красиво
                AudioManager.AUDIOFOCUS_GAIN -> {
                    if (mediaSessionCallback.currentState == PlaybackStateCompat.STATE_PLAYING) {
                        exoPlayer?.volume = 1.0f
//                        mediaSessionCallback.onPlay()
                    }
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    exoPlayer?.volume = 0.5f
                }
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK -> {
                    Log.d(TAG, "AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK: ")
                }
                else -> {
                    mediaSessionCallback.onPause()
                }
            }
        }

    //
    private val becomingNoisyReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) { // Disconnecting headphones - stop playback
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent.action) {
                mediaSessionCallback.onPause()
            }
        }
    }

    inner class PlayerServiceBinder : Binder() {

        fun getMediaSessionToke(): MediaSessionCompat.Token {
            return mediaSession!!.sessionToken
        }

        fun getPlayer(): ExoPlayer? {
            return exoPlayer
        }

        fun setPodcastWithPosition(podcast: Podcast, position: Long) {
            Log.d(TAG, "setPodcastWithPosition: $podcast posit $position")
            playbackPosition = position

            this@MusicService.podcast = podcast
        }

        /**
         * Перематываем воспроизведение вперед на N секунд
         */
        fun moveForward() {
            exoPlayer?.apply {
                seekTo(this.currentPosition + 10000)
            }
        }

        /**
         * Перематываем воспроизведение на N секунд назад
         */
        fun moveBack() {
            exoPlayer?.apply {
                seekTo(this.currentPosition - 10000)
            }
        }

    }

    private fun refreshNotificationAndForegroundStatus(playbackState: Int) {
        when (playbackState) {
            PlaybackStateCompat.STATE_PLAYING -> {
                startForeground(NOTIFICATION_ID, getNotification(playbackState))
            }
            PlaybackStateCompat.STATE_PAUSED -> {
                NotificationManagerCompat.from(this@MusicService)
                    .notify(NOTIFICATION_ID, getNotification(playbackState)!!)
                stopForeground(false)
            }
            else -> {
                stopForeground(true)
            }
        }
    }

    /**
     * при старте сервиса стразу выводим нотификашку, чтобы норм работало на новых андройдах
     */
    private fun createInitNotification() {
        val builder =
            MediaStyleHelper.from(
                this,
                mediaSession
            )

        builder.priority = NotificationCompat.PRIORITY_HIGH
        builder.setOnlyAlertOnce(true)
        builder.setChannelId(NOTIFICATION_DEFAULT_CHANNEL_ID)
        builder.setSmallIcon(R.drawable.bat_notif_icon_white)

        startForeground(NOTIFICATION_ID, builder.build())
    }

    private fun getNotification(playbackState: Int): Notification? {
        val builder =
            MediaStyleHelper.from(
                this,
                mediaSession
            )

        if (playbackState == PlaybackStateCompat.STATE_PLAYING) builder.addAction(
            NotificationCompat.Action(
                android.R.drawable.ic_media_pause,
                "pause",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_PLAY_PAUSE,

                )
            )
        ) else builder.addAction(
            NotificationCompat.Action(
                android.R.drawable.ic_media_play,
                "play",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            )
        )

        builder.setStyle(
            androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0)
                .setShowCancelButton(true)
                .setCancelButtonIntent(
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this,
                        PlaybackStateCompat.ACTION_STOP
                    )
                )
                .setMediaSession(mediaSession!!.sessionToken)
        )

        builder.setShowWhen(false)

        builder.priority = NotificationCompat.PRIORITY_HIGH
        builder.setOnlyAlertOnce(true)
        builder.setChannelId(NOTIFICATION_DEFAULT_CHANNEL_ID)
        builder.setSmallIcon(R.drawable.bat_notif_icon_white)
        return builder.build()
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaSession!!.release()
        exoPlayer!!.release()
        ServiceLocator.provideExoCache().release()
//        cache = null
    }

    fun updateCurrePodcastPosit(position: Long) {
        if (podcast != null) {
            repositoryS.updatePodcastLastPos(podcast!!.podcastId, position)
        }
    }

//    private fun playbackStateListner() = object : Listener {
//
//    }

}