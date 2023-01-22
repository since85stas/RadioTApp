package stas.batura.radiotproject.ui.news

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import stas.batura.data.NewsBody

@BindingAdapter("newsTitleBind")
fun TextView.newsTitleBind(newsBody: NewsBody) {
    text = "${newsBody.title}"
}

@BindingAdapter("newsBodyBind")
fun TextView.newsBodyBind(newsBody: NewsBody) {
    text = "${newsBody.snippet}"
}

@BindingAdapter("newsImageVisibility")
fun ImageView.newsImageVisibility(newsBody: NewsBody) {
    if (newsBody.pic == null || newsBody.pic == "") {
        visibility = View.GONE
    } else {
        visibility = View.VISIBLE
    }
}
