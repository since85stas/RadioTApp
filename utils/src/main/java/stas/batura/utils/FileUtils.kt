package stas.batura.utils

import timber.log.Timber
import java.io.File

fun deleteLocalFile(path: String) {
    val file = File(path)

    if (file.exists()) {

    } else {
        Timber.d("local file not exist")
    }
}