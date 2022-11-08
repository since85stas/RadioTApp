package stas.batura.download

sealed class DownloadResult {
    class OK(): DownloadResult()
    class Error(string: String): DownloadResult()
}
