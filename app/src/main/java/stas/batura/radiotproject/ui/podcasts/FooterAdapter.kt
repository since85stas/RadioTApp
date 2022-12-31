package stas.batura.radiotproject.ui.podcasts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import stas.batura.data.SavedPodcast
import stas.batura.radiotproject.R
import stas.batura.radiotproject.databinding.PodcastFooterBinding
import stas.batura.radiotproject.databinding.SavedPodcastItemBinding
import stas.batura.radiotproject.ext.setDebounceOnCLick
import stas.batura.radiotproject.utils.DebouncingOnClickListener
import timber.log.Timber

class FooterAdapter(private val addClick: () -> Unit): RecyclerView.Adapter<FooterAdapter.FooterViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FooterViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = PodcastFooterBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        binding.addMore.setDebounceOnCLick(2000L) {
            addClick()
        }
        return FooterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FooterViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return 1
    }

    class FooterViewHolder(val binding: PodcastFooterBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind() {
            Timber.d("")
        }

    }
}