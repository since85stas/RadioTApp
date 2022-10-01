package stas.batura.protostore

import kotlinx.coroutines.flow.Flow
import stas.batura.data.ListViewType
import stas.batura.data.Year

interface Preference {

    fun setNumPodcsts(num: Int)

    fun getPrefActivePodcastNum(): Flow<Int>

    fun setPrefActivePodcastNum(num: Int)

    fun setPrefListType(type: ListViewType)

    fun setPrefNumOnPage(num: Int)

    fun setPrefSelectedYear(year: Year)

}