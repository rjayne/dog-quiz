package au.com.jayne.dogquiz.common.util

import android.content.SharedPreferences
import au.com.jayne.dogquiz.domain.model.SharedPreferenceKey
import timber.log.Timber
import javax.inject.Inject

class SharedPreferenceStorage @Inject constructor(sharedPreferences: SharedPreferences){

    private val sharedPreferenceAccessor: SharedPreferenceAccessor = SharedPreferenceAccessor(sharedPreferences)

    fun isSoundEnabled(): Boolean {
        return sharedPreferenceAccessor.getBooleanFromPreferences(SharedPreferenceKey.SOUNDS_ENABLED, true)
    }

    fun resetHighScores() {
        Timber.d("resetHighScores")
        sharedPreferenceAccessor.clearValue(SharedPreferenceKey.HIGH_SCORES)
    }
}