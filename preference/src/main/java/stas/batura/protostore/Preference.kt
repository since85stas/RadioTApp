package stas.batura.protostore

import kotlinx.coroutines.flow.Flow
import stas.batura.data.ListViewType
import stas.batura.data.Year

interface Preference {

    fun setNumPodcsts(num: Int)

    fun getUserPrefPNumber(): Flow<Int>

    fun getPrefActivePodcastNum(): Flow<Int>

    fun setPrefActivePodcastNum(num: Int)

    fun setPrefListType(type: ListViewType)

    fun setPrefNumOnPage(num: Int)

    fun setPrefSelectedYear(year: Year)

    fun getPrefSelectedYear(): Flow<Year>

    fun getPrefListType(): Flow<ListViewType>

    fun setPrefLastPnumb(numb: Int)

    fun setPrefMaxPnumb(numb: Int)

    fun getPrefMaxPnumb(): Flow<Int>

    suspend fun setFistOpen(boolean: Boolean)

    fun isFirstOpen(): Flow<Boolean>

    fun getPrefLastPnumb(): Flow<Int>

}