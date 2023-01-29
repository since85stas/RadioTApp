package stas.batura.room.podcast

import androidx.room.TypeConverter
import stas.batura.data.SavedStatus

class SavedStatusConverter {

    @TypeConverter()
    fun fromStatusToByte(status: SavedStatus?): Byte? {
        if (status != null) {
            return status.ordinal.toByte()
        } else {
            return null
        }
    }

    @TypeConverter
    fun fromByteToStatus(status: Byte?): SavedStatus? {
        if (status != null) {
            return SavedStatus.getByValue(status)
        } else {
            return null
        }
    }

}