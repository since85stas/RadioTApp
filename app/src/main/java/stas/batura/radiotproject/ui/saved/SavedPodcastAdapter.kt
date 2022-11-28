package stas.batura.radiotproject.ui.saved

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import stas.batura.data.SavedPodcast
import stas.batura.radiotproject.databinding.SavedPodcastItemBinding

class SavedPodcastAdapter( private val deleteClick: (SavedPodcast) -> Unit ) : ListAdapter<SavedPodcast, SavedPodcastAdapter.ViewHolder>(SaveDiffCalback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SavedPodcastItemBinding.inflate(
            layoutInflater,
            parent,
            false
        )

        return ViewHolder(binding, deleteClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        val binding: SavedPodcastItemBinding,
        val deleteFunck: (SavedPodcast) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(podcast: SavedPodcast) {
            binding.savedPodcast = podcast

            binding.downloadButton.setOnClickListener() {
                deleteFunck(podcast)
            }
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