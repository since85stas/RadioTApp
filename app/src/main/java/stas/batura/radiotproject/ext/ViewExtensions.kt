package stas.batura.radiotproject.ext

import android.view.View
import stas.batura.radiotproject.utils.DebouncingOnClickListener

fun View.setDebounceOnCLick(intervalMillis: Long = 0, doClick: (View) -> Unit) =
    setOnClickListener(DebouncingOnClickListener(intervalMillis = intervalMillis, doClick = doClick))