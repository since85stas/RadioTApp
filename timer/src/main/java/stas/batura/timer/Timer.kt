package stas.batura.timer

import kotlinx.coroutines.flow.Flow

interface Timer {

    fun getValues(): Flow<Long>

    fun startTimer()

    fun stopTimer()
}