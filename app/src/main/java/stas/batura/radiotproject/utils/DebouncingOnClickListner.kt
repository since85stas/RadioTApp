package stas.batura.radiotproject.utils

import android.view.View

class DebouncingOnClickListener(
    private val intervalMillis: Long,
    private val doClick: ((View) -> Unit)
) : View.OnClickListener {

    override fun onClick(v: View) {
        if (enabled) {
            enabled = false
            v.postDelayed(ENABLE_AGAIN, intervalMillis)
            doClick(v)
        }
    }

    companion object {
        @JvmStatic
        var enabled = true
        private val ENABLE_AGAIN =
            Runnable { enabled = true }
    }
}