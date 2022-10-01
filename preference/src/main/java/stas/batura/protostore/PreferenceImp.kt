package stas.batura.protostore

import android.content.Context
import androidx.datastore.createDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import stas.batura.data.ListViewType
import stas.batura.data.Year
import stas.batura.radiotproject.protostore.UserPreferences

class PreferenceImp(context: Context) {

    private val protoData = context.createDataStore(
    fileName = "user_prefs.pb",
    serializer = UserPreferencesSerializer
    )

    private val repScope = CoroutineScope(Dispatchers.IO)

    /***
     * получаем число отображаемых подкастов
     */
    fun getUserPrefPNumber(): Flow<Int> {

        return protoData.data.map { it ->
            it.numShownPodcasts
        }
    }

    /**
     * записываем число показоваемых треков в настройки
     */
    override fun setNumPodcsts(num: Int) {
        repScope.launch {
            protoData.updateData { t: UserPreferences ->
                t.toBuilder().setNumShownPodcasts(num).build()
            }
        }
    }

    /**
     * устанавливаем по какому типу отображать подкасты
     * @param type тип выводимого списка
     */
    override fun setPrefListType(type: ListViewType) {
        repScope.launch {
            protoData.updateData { t: UserPreferences ->
                t.toBuilder().setListViewType(type.ordinal).build()
            }
        }
    }

    /**
     * получаем по какому типу отображать подкасты
     */
    fun getPrefListType(): Flow<ListViewType> {
        return protoData.data.map {
            ListViewType.getByValue(it.listViewType)!!
        }
    }

    /**
     * получаем номер активный подкаст
     */
    override fun getPrefActivePodcastNum(): Flow<Int> {
        return protoData.data.map {
            it.activePodcNum
        }
    }

    /**
     * отмечаем что трек играет, значит он считается активным и берется по умолчанию
     * @param num номер активного подкаста
     */
    override fun setPrefActivePodcastNum(num: Int) {
        repScope.launch {
            protoData.updateData { t: UserPreferences ->
                t.toBuilder().setActivePodcNum(num).build()
            }
        }
    }

    /**
     * устанавливаем число отображаемых подкастов на странице
     * @param num число выводимых подкастов
     */
    override fun setPrefNumOnPage(num: Int) {
        repScope.launch {
            protoData.updateData { t: UserPreferences ->
                t.toBuilder().setNumberOnPage(num).build()
            }
        }
    }

    /**
     * записываем выбранный для отображения год
     * @param year год
     */
    override fun setPrefSelectedYear(year: Year) {
        repScope.launch {
            protoData.updateData { t: UserPreferences ->
                t.toBuilder().setSelectedYear(year.ordinal).build()
            }
        }
    }

    /**
     * получаем выбранный для отображения год
     */
    fun getPrefSelectedYear(): Flow<Year> {
        return protoData.data.map {
            Year.getByValue(it.selectedYear)!!
        }
    }

}