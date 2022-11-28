package stas.batura.download

import java.io.Serializable

sealed class DownloadResult: Serializable {
    class OK(): DownloadResult(), Serializable
    class Error(string: String): DownloadResult(), Serializable
}
