package stas.batura.download

import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2core.DownloadBlock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * хэндлер для обработки загрузок кэшируемых медиа
 */
class DownloadHandler(): FetchListener  {

    override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
        Timber.d("download is quaede: $download $waitingOnNetwork")
    }

    override fun onCancelled(download: Download) {
        Timber.d("download is canceled: $download")
        _progress.postValue(null)
    }

    override fun onCompleted(download: Download) {
        Timber.d("download completed, ${download.file}")
        logInfo("download completed, ${download.file}")
        val media = _mediaToCache.find { it.id == download.tag?.toLong() }

        currentPlaylist?.setMediaIsCahed(download.tag?.toLong())
        _progress.postValue(null)

        if (downloadsNumber > 2) {
            downloadsNumber--
        } else if (downloadsNumber == 2) {
//                CoroutineScope(Dispatchers.IO).launch {
//                    service!!.taskIsCompleted(currentTaskId, TaskResult(), authConfig)
//                    Timber.i("Task is complete: id=${currentTaskId} ${"a"}")
//                    logInfo("Task is complete: id=${currentTaskId} ${"a"}")
//                }
            completeCurrentTask()
        }

        if (media != null && appState.value == AppState.DEMO_CONTENT_PLAYER) {
//                addToRotation(media)
            _demoMedia.value = media
            _demoMedia.value = null
        }

        ioScope.launch {
            delay(300)
            sendStrorageStats()
        }
    }

    override fun onDeleted(download: Download) {
        Timber.d("download is deleted")
    }

    override fun onDownloadBlockUpdated(
        download: Download,
        downloadBlock: DownloadBlock,
        totalBlocks: Int
    ) {
    }

    override fun onAdded(download: Download) {
        Timber.d("Downloading added: $download")
    }

    override fun onPaused(download: Download) {
        Timber.d("Downloading paused: $download")
    }

    override fun onError(download: Download, error: Error, throwable: Throwable?) {
        Timber.e("Downloading error: $download, $throwable")
        _progress.postValue(null)

        compileTaskCrash("Downloading error: $download, $throwable", currentTaskId)
    }

    override fun onRemoved(download: Download) {
        Timber.d("dowload is removed")
    }

    override fun onProgress(
        download: Download,
        etaInMilliSeconds: Long,
        downloadedBytesPerSecond: Long
    ) {
        Timber.d("download progress: ${download.progress} perSec: $downloadedBytesPerSecond")
        _progress.postValue(download.progress)
        if (download.progress >= 100) _progress.postValue(null) //reset
    }

    override fun onResumed(download: Download) {
    }

    override fun onStarted(
        download: Download,
        downloadBlocks: List<DownloadBlock>,
        totalBlocks: Int
    ) {
        Timber.d("download started $download")
    }

    override fun onWaitingNetwork(download: Download) {
        Timber.d("download wait: onWaitingNetwork")
    }
}