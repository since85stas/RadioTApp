package stas.batura.download

import java.io.Serializable

data class DownloadServiceResult(
    val entityId: Int,
    val status: DownloadResult,
    val cachedPath: String,
): Serializable
