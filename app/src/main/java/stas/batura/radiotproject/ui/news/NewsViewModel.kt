package stas.batura.radiotproject.ui.news

import androidx.lifecycle.ViewModel
import stas.batura.di.ServiceLocator

class NewsViewModel: ViewModel() {

    val newsRepository = ServiceLocator.provideNewsRepository()

}