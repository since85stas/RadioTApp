package stas.batura.radiotproject.ui.podcasts

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.podcast_item_view_detailed.view.*
import stas.batura.radiotproject.MainActivityViewModel
import stas.batura.radiotproject.R
import stas.batura.radiotproject.databinding.PodcastItemViewDetailedBinding
import stas.batura.data.Podcast
import stas.batura.data.SavedStatus

class PodcastsAdapter(
    val mainActivityViewModel: MainActivityViewModel,
    val listModel: PodcastListViewModel
) :
    ListAdapter<Podcast, PodcastsAdapter.ViewHolder>(TrackDiffCalback()) {

    override fun onCurrentListChanged(
        previousList: MutableList<Podcast>,
        currentList: MutableList<Podcast>
    ) {
        super.onCurrentListChanged(previousList, currentList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = PodcastItemViewDetailedBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return ViewHolder(binding, mainActivityViewModel, listModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        val binding: PodcastItemViewDetailedBinding,
        val mainActivityViewModel: MainActivityViewModel,
        val listModel: PodcastListViewModel
    ) :
        RecyclerView.ViewHolder(binding.root){

        var animator: ObjectAnimator? = null

        fun bind(podcast: Podcast) {
            binding.podcast = podcast
            binding.mainModel = mainActivityViewModel
            binding.podcastViewModel = listModel

            // адаптер для списка тем
            val adapter = TimeStampsAdapter(mainActivityViewModel, podcast)
            binding.root.timelabeles_recycler.adapter = adapter
            adapter.submitList(podcast.timeLabels)

            Glide.with(binding.root.context)
                    .load(podcast.imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.root.image_container)

            binding.downloadImage.setOnClickListener {
                mainActivityViewModel.startDownloadPodcast(podcast)
            }

            if (podcast.savedStatus == SavedStatus.LOADING) {
                binding.downloadImage.apply {
                    animator = ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0.2f)
                    animator?.duration = 1300
                    animator?.repeatCount = ObjectAnimator.INFINITE
                    animator?.repeatMode = ObjectAnimator.REVERSE
                    animator?.start()
                }
            } else {
                if (animator != null) {
                    binding?.downloadImage?.alpha = 1f
                    animator?.cancel()
                }
            }

            if (podcast.isActive) {
                binding.cardView.strokeColor = binding.root.context.resources.getColor(R.color.colorAccent)
                binding.cardView.strokeWidth = 5
            } else {
                binding.cardView.strokeWidth = 0
            }

            binding.executePendingBindings()

        }

    }

    class TrackDiffCalback : DiffUtil.ItemCallback<Podcast>() {



        override fun areItemsTheSame(
            oldItem: Podcast,
            newItem: Podcast
        ): Boolean {
            val comp =  oldItem.podcastId == newItem.podcastId
            return comp
        }

        override fun areContentsTheSame(
            oldItem: Podcast,
            newItem: Podcast
        ): Boolean {
            val comp = oldItem == newItem
            return comp
        }
    }


}