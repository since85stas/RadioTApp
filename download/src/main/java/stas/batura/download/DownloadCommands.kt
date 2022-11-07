package stas.batura.download

interface DownloadCommands {

    fun progress(preogressPercent: Int)

    fun sendMessage()
}