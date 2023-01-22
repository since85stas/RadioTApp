package stas.batura.radiotproject.ui.news

import android.widget.TextView
import androidx.databinding.BindingAdapter
import stas.batura.data.NewsBody

@BindingAdapter("newsTitleBind")
fun TextView.newsTitleBind(newsBody: NewsBody) {
    text = "${newsBody.title}: "
}