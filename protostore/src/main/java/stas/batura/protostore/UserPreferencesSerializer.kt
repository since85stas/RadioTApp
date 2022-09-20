package stas.batura.protostore

import androidx.datastore.CorruptionException
import androidx.datastore.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import stas.batura.radiotproject.protostore.UserPreferences
import java.io.InputStream
import java.io.OutputStream

object UserPreferencesSerializer : Serializer<UserPreferences> {
    override fun readFrom(input: InputStream): UserPreferences {
        try {
            return UserPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override fun writeTo(t: UserPreferences, output: OutputStream) = t.writeTo(output)
}