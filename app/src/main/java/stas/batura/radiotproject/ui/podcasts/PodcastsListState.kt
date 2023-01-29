package stas.batura.radiotproject.ui.podcasts

import stas.batura.data.ListViewType
import stas.batura.data.Podcast

data class PodcastsListState(
    val podcasts: List<Podcast>,
    val viewType: ListViewType
)
