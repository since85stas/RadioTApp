package stas.batura.radioproject.ui.podcastlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import stas.batura.radiotproject.R

class HeaderAdapter: RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.podcast_list_header, parent, false)
        return HeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind(10)
    }

    override fun getItemCount(): Int {
        return 1
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val flowerNumberTextView: TextView = itemView
            .findViewById(R.id.numbersText)

        fun bind(flowerCount: Int) {
            flowerNumberTextView.text = flowerCount.toString()
        }
    }
}