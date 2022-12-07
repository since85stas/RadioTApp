package stas.batura.radiotproject.ui.podcasts

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.timelable_item_view.view.*
import stas.batura.radiotproject.MainActivityViewModel
import stas.batura.radiotproject.databinding.TimelableItemViewBinding
import stas.batura.retrofit.TimeLabel
import stas.batura.room.podcast.Podcast


class TimeStampsAdapter (
    private val mainActivityViewModel: MainActivityViewModel,
    private  val podcast: Podcast
): ListAdapter<TimeLabel, TimeStampsAdapter.ViewHolder>(TrackDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, mainActivityViewModel, podcast)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder (
        val binding: TimelableItemViewBinding,
        val mainActivityViewModel: MainActivityViewModel,
        val podcast: Podcast) :
        RecyclerView.ViewHolder (binding.root) {

        fun bind (timeLabel: TimeLabel) {
            binding.timeLable = timeLabel
            binding.mainviewModel = mainActivityViewModel
            binding.podcast = podcast
            binding.startTime = timeLabel.newStartTime
//            binding.mainModel = mainActivityViewModel
            binding.executePendingBindings()

            // открываем страницу с новостью
            binding.root.topic_title.setOnClickListener {
                val intent =                     Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(podcast.bodyHtml!![adapterPosition])
                )
                try {
                    binding.root.context.startActivity(intent)
                } catch (e : ActivityNotFoundException) {
                    Log.d("Timestamp", "bind: $e")
                }

            }
        }

        companion object {
            fun from(
                parent: ViewGroup,
                mainActivityViewModel: MainActivityViewModel,
                podcast: Podcast):
                    ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TimelableItemViewBinding.inflate(layoutInflater,
                    parent,
                    false)
                return ViewHolder(binding, mainActivityViewModel, podcast)
            }
        }
    }

    class TrackDiffCallback : DiffUtil.ItemCallback<TimeLabel> (){

        override fun areItemsTheSame(
            oldItem: TimeLabel,
            newItem: TimeLabel
        ): Boolean {
            return oldItem.topic == newItem.topic
        }

        override fun areContentsTheSame(
            oldItem: TimeLabel,
            newItem: TimeLabel
        ): Boolean {
            return  oldItem == newItem
        }
    }



}