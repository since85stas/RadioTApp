package stas.batura.download

import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2core.DownloadBlock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * хэндлер для обработки загрузок кэшируемых медиа
 */
class DownloadHandler(private val downloadCommands: DownloadCommands): FetchListener  {

    override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
        Timber.d("download is quaede: $download $waitingOnNetwork")
    }

    override fun onCancelled(download: Download) {
        Timber.d("download is canceled: $download")
        downloadCommands.sendMessage(DownloadResult.Error("canceled"))
    }

    override fun onCompleted(download: Download) {
        Timber.d("download completed, ${download.file}")
        downloadCommands.sendMessage(DownloadResult.OK())
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
        downloadCommands.sendMessage(DownloadResult.Error(error.toString()))
    }

    override fun onRemoved(download: Download) {
        Timber.d("dowload is removed")
    }

    override fun onProgress(
        download: Download,
        etaInMilliSeconds: Long,
        downloadedBytesPerSecond: Long
    ) {
        downloadCommands.progress(download.progress)
        Timber.d("download progress: ${download.progress} perSec: $downloadedBytesPerSecond")
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