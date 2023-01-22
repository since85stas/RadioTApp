package stas.batura.radiotproject.ui.news

import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import stas.batura.data.NewsBody
import timber.log.Timber
import java.net.URI

@BindingAdapter("newsTitleBind")
fun TextView.newsTitleBind(newsBody: NewsBody) {
    text = "${newsBody.title}"
}

@BindingAdapter("newsSiteBind")
fun TextView.newsSiteBind(newsBody: NewsBody) {
    if (newsBody.link != null && newsBody.link != "") {
        try {
            val uri = URI(newsBody.link)
            val domain: String = uri.host
            val spannableString = SpannableString(domain)
            spannableString.setSpan(
                UnderlineSpan(),
                0,
                domain.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            text = spannableString
        } catch (e: java.lang.Exception) {
            Timber.e(e.toString())
        }
    } else {
        visibility = View.GONE
    }
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
