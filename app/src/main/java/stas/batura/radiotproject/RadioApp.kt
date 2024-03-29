package stas.batura.radiotproject

import android.app.Application
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.ExoPlayer
import stas.batura.di.ServiceLocator
import stas.batura.radiotproject.player.MusicService

class RadioApp(): Application() {

    init {
        ServiceLocator.setContext(this)
    }

    /**
     *
     */
    companion object ServiceHelper {

        private val TAG = "MusicService"

        var serviceBinder: MusicService.PlayerServiceBinder? = null

        private var serviceConnection: ServiceConnection? = null

        var mediaController: MediaControllerCompat? = null

        val callbackChanges: MutableLiveData<PlaybackStateCompat?> = MutableLiveData(null)

        val exoPlayer: MutableLiveData<ExoPlayer> = MutableLiveData()

        private val callback = object : MediaControllerCompat.Callback() {
                override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                    callbackChanges.value = state
                }
            }

        fun getServiceConnection(): ServiceConnection {
            if (serviceConnection != null) {
                return serviceConnection!!
            } else {

                val s = object : ServiceConnection {
                    override fun onServiceConnected(name: ComponentName, service: IBinder) {

                        try {
                            serviceBinder = service as MusicService.PlayerServiceBinder

                            mediaController = MediaControllerCompat(
                                ServiceLocator.provideContext(),
                                serviceBinder!!.getMediaSessionToke()
                            )

                            exoPlayer.value = serviceBinder?.getPlayer()

                            mediaController?.registerCallback(callback)
                            callback.onPlaybackStateChanged(mediaController?.playbackState)
                        } catch (e: RemoteException) {
                            Log.e(TAG, "onServiceConnected: $e",)
                        }
                    }

                    override fun onServiceDisconnected(name: ComponentName) {
                        serviceBinder = null
                        serviceConnection = null
                        if (mediaController != null) {
                            mediaController!!.unregisterCallback(callback)
                            mediaController = null
                        }
                    }
                }
                return s
            }
        }
    }



}