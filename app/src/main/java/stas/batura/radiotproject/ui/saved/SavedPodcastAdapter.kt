package stas.batura.radiotproject.ui.saved

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.podcast_item_view_detailed.view.*
import stas.batura.data.SavedPodcast
import stas.batura.radioproject.ui.podcastlist.PodcastListViewModel
import stas.batura.radiotproject.MainActivityViewModel
import stas.batura.radiotproject.databinding.PodcastItemViewDetailedBinding
import stas.batura.radiotproject.databinding.SavedPodcastItemBinding
import stas.batura.radiotproject.ui.podcasts.TimeStampsAdapter
import stas.batura.room.podcast.Podcast

class SavedPodcastAdapter : ListAdapter<SavedPodcast, SavedPodcastAdapter.ViewHolder>(SaveDiffCalback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SavedPodcastItemBinding.inflate(
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
        val binding: SavedPodcastItemBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(podcast: SavedPodcast) {
            binding.savedPodcast = podcast
        }

    }

    class SaveDiffCalback : DiffUtil.ItemCallback<SavedPodcast>() {

        override fun areItemsTheSame(
            oldItem: SavedPodcast,
            newItem: SavedPodcast
        ): Boolean {
            return oldItem.podcastId == newItem.podcastId
        }

        override fun areContentsTheSame(
            oldItem: SavedPodcast,
            newItem: SavedPodcast
        ): Boolean {
            return oldItem == newItem
        }
    }

}