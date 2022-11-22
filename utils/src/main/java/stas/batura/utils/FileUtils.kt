package stas.batura.utils

import timber.log.Timber
import java.io.File

fun deleteLocalFile(path: String) {
    val file = File(path)

    if (file.exists()) {
        val status = file.delete()
        if (status) {
            Timber.d("delete is ok: $path")
        } else {
            Timber.d("delete problem: $path")
        }
    } else {
        Timber.d("local file not exist: $path")
    }
}