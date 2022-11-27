package stas.batura.radioproject.ui.podcastlist

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import stas.batura.radiotproject.MainActivityViewModel
import stas.batura.radiotproject.databinding.PodcastItemViewDetailedBinding
import stas.batura.radiotproject.ui.podcasts.TimeStampsAdapter
import stas.batura.room.podcast.Podcast

@OptIn(ExperimentalCoroutinesApi::class)
class PodcastsAdapter(
    val mainActivityViewModel: MainActivityViewModel,
    val listModel: PodcastListViewModel
) :
    ListAdapter<Podcast, PodcastsAdapter.ViewHolder>(TrackDiffCalback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, mainActivityViewModel, listModel)
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

        fun bind(podcast: Podcast) {
            binding.podcast = podcast
            binding.mainModel = mainActivityViewModel
            binding.podcastViewModel = listModel

            // адаптер для списка тем
            val adapter = TimeStampsAdapter(mainActivityViewModel, podcast)
            binding.root.timelabeles_recycler.adapter = adapter
            adapter.submitList(podcast.timeLabels)

            binding.executePendingBindings()

            // если это выбранный обект меняюем вид
//            if (podcast.podcastId == listModel.activeNumPref.value) {
////                binding.logoImage.setImageResource(R.drawable.ic_pause_black_24dp)
//            } else {
                Glide.with(binding.root.context)
                    .load(podcast.imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.root.image_container)
//            }

//            // если в данный момент проигрывается то включаем анимацию
//            if (podcast.podcastId == listModel.activeNumPref.value && mainActivityViewModel.spinnerPlay.value == true) {
//                Log.d("PodcastAdapter", "bind: $listModel.activeNumPref.value")
//                binding.spinnerPlay.visibility = View.VISIBLE
//            } else {
//                binding.spinnerPlay.visibility = View.GONE
//            }

            binding.downloadImage.setOnClickListener {
                mainActivityViewModel.startDownloadPodcast(podcast)

                val animator = ObjectAnimator.ofFloat(it, View.ALPHA, 0.2f)
                animator.duration = 1000
                animator.repeatCount = ObjectAnimator.INFINITE
                animator.repeatMode = ObjectAnimator.REVERSE
                animator.start()
            }

//            val animator = ObjectAnimator.ofFloat(binding.downloadImage, View.ROTATION, -360f, 0f)
//            animator.duration = 5000
//            animator.addListener(object : AnimatorListenerAdapter() {
//                override fun onAnimationStart(animation: Animator) {
//                    Log.d("t", "r")
//                    super.onAnimationStart(animation)
//                }
//            })
//            animator.start()
        }

        companion object {
            fun from(
                parent: ViewGroup,
                mainActivityViewModel: MainActivityViewModel,
                listModel: PodcastListViewModel): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PodcastItemViewDetailedBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
                return ViewHolder(binding, mainActivityViewModel, listModel)
            }
        }


    }

    class TrackDiffCalback : DiffUtil.ItemCallback<Podcast>() {

        override fun areItemsTheSame(
            oldItem: Podcast,
            newItem: Podcast
        ): Boolean {
            return oldItem.podcastId == newItem.podcastId
        }

        override fun areContentsTheSame(
            oldItem: Podcast,
            newItem: Podcast
        ): Boolean {
            return oldItem == newItem
        }
    }


}