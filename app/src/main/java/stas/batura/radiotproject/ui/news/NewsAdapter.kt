package stas.batura.radiotproject.ui.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import stas.batura.data.NewsBody
import stas.batura.radiotproject.databinding.NewsItemViewBinding
import stas.batura.radiotproject.databinding.PodcastItemViewDetailedBinding
import stas.batura.radiotproject.ui.podcasts.PodcastsAdapter
import stas.batura.room.podcast.Podcast

class NewsAdapter(): ListAdapter<NewsBody, NewsAdapter.ViewHolder>(NewsDiffCalback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = NewsItemViewBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        val binding: NewsItemViewBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(newsBody: NewsBody) {
            binding.newsBody = newsBody
            if (newsBody.pic != null) {
                Glide.with(binding.root.context).load(newsBody.pic).into(binding.newsImageView)
            }
        }
    }

    class NewsDiffCalback : DiffUtil.ItemCallback<NewsBody>() {

        override fun areItemsTheSame(
            oldItem: NewsBody,
            newItem: NewsBody
        ): Boolean {
            val comp =  oldItem.title == newItem.title
            return comp
        }

        override fun areContentsTheSame(
            oldItem: NewsBody,
            newItem: NewsBody
        ): Boolean {
            val comp = oldItem == newItem
            return comp
        }
    }

}