package au.com.jayne.dogquiz.common.util

import android.content.SharedPreferences
import au.com.jayne.dogquiz.domain.model.SharedPreferenceKey
import java.util.*

class SharedPreferenceAccessor(private val sharedPreferences: SharedPreferences) {

    fun getIntFromPreferences(key: SharedPreferenceKey, defaultValue: Int): Int {
        return sharedPreferences.getInt(key.keyName, defaultValue)
    }

    fun getStringFromPreferences(key: SharedPreferenceKey): String? {
        return sharedPreferences.getString(key.keyName, null)
    }

    fun getStringFromPreferences(key: SharedPreferenceKey, defaultValue: String): String {
        return sharedPreferences.getString(key.keyName, defaultValue)!! // not nullable when default value supplied
    }

    fun getBooleanFromPreferences(key: SharedPreferenceKey, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key.keyName, defaultValue)
    }

    fun getByteArrayFromPreferences(key: SharedPreferenceKey): ByteArray? {
        val bytesAsString = sharedPreferences.getString(key.keyName, null)

        bytesAsString?.let {
            val split = it.substring(1, it.length - 1).split(", ")
            val array = ByteArray(split.size)
            for (i in split.indices) {
                array[i] = java.lang.Byte.parseByte(split[i])
            }

            return array
        }

        return null
    }

    fun clearValue(key: SharedPreferenceKey) {
        sharedPreferences.edit().remove(key.keyName).commit()
    }

    fun editSharedPreferences(key: SharedPreferenceKey, value: Any) {
        val editor = sharedPreferences.edit()
        when(value) {
            is String -> {
                editor.putString(key.keyName, value)
            }
            is Int -> {
                editor.putInt(key.keyName, value)
            }
            is Boolean -> {
                editor.putBoolean(key.keyName, value)
            }
            is ByteArray -> {
                editor.putString(key.keyName, Arrays.toString(value))
            }
        }
        editor.apply()
    }
}