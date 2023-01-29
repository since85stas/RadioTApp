package stas.batura.radiotproject.ui.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import stas.batura.data.NewsBody
import stas.batura.di.ServiceLocator
import timber.log.Timber

class NewsViewModel: ViewModel() {

    val newsRepository = ServiceLocator.provideNewsRepository()

    private val _spinner = MutableLiveData<Boolean>(false)
    val spinner: LiveData<Boolean>
        get() = _spinner

    private val _news = MutableLiveData<List<NewsBody>>()
    val news: LiveData<List<NewsBody>>
        get() = _news

    init {
        launchDataLoad {
            val newsData = newsRepository.getLastNews()
            _news.postValue(newsData)
        }
    }


    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            try {
                _spinner.postValue(true)
                block()
            } catch (error: Throwable) {
                Timber.d( "launchDataLoad: " + error)
            } finally {
                _spinner.postValue(false)
            }
        }
    }

}