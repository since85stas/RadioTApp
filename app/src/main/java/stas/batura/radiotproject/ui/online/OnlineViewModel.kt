package stas.batura.radiotproject.ui.online

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import stas.batura.di.ServiceLocator

class OnlineViewModel: ViewModel() {

    val timerValues = ServiceLocator.provideTimerValues().asLiveData()

}