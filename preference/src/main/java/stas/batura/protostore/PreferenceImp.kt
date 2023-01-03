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

class PreferenceImp(context: Context): Preference {

    private val protoData = context.createDataStore(
    fileName = "user_prefs.pb",
    serializer = UserPreferencesSerializer
    )

    private val prefScope = CoroutineScope(Dispatchers.Default)

    /***
     * получаем число отображаемых подкастов
     */
    override fun getUserPrefPNumber(): Flow<Int> {

        return protoData.data.map { it ->
            it.numShownPodcasts
        }
    }

    /**
     * записываем число показоваемых треков в настройки
     */
    override fun setNumPodcsts(num: Int) {
        prefScope.launch {
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
        prefScope.launch {
            protoData.updateData { t: UserPreferences ->
                t.toBuilder().setListViewType(type.ordinal).build()
            }
        }
    }

    /**
     * получаем по какому типу отображать подкасты
     */
    override fun getPrefListType(): Flow<ListViewType> {
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
        prefScope.launch {
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
        prefScope.launch {
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
        prefScope.launch {
            protoData.updateData { t: UserPreferences ->
                t.toBuilder().setSelectedYear(year.ordinal).build()
            }
        }
    }

    /**
     * получаем выбранный для отображения год
     */
    override fun getPrefSelectedYear(): Flow<Year> {
        return protoData.data.map {
            Year.getByValue(it.selectedYear)!!
        }
    }

    override fun setPrefLastPnumb(numb: Int) {
        prefScope.launch {
            protoData.updateData { t: UserPreferences ->
                t.toBuilder().setLastPodcNumb(numb).build()
            }
        }
    }

    /**
     * сохраняем номер последнего подкаста в БД
     * @param numb номер подкаста
     */
    override fun setPrefMaxPnumb(numb: Int) {
        prefScope.launch {
            protoData.updateData { t: UserPreferences ->
                t.toBuilder().setMaxPodcNumb(numb).build()
            }
        }
    }

    override fun getPrefMaxPnumb(): Flow<Int> {
        return protoData.data.map {
            it.maxPodcNumb
        }
    }

    /**
     * устанавливаем после первого открытия программы
     * @param boolean если True - значит уже запустили
     */
    override suspend fun setFistOpen(boolean: Boolean) {
        protoData.updateData { t: UserPreferences ->
            t.toBuilder().setIsNotFirstOpen(boolean).build()
        }
    }

    /**
     * проверяем первый ли это запуск
     */
    override fun isFirstOpen(): Flow<Boolean> {
        return protoData.data.map {
            !it.isNotFirstOpen
        }
    }

    /**
     * получаем выбранный для отображения год
     */
    override fun getPrefLastPnumb(): Flow<Int> {
        return protoData.data.map {
            it.lastPodcNumb
        }
    }

}