package stas.batura.data

data class SavedPodcast(
    val podcastId: Int,
    val localPath: String,
    val time: Long,
    val name: String,
) {

}