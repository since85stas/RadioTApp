package stas.batura.timer

import kotlinx.coroutines.flow.Flow

interface Timer {

    fun getValues(): Flow<Int>

    fun startTimer()

    fun stopTimer()
}