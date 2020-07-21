package au.com.jayne.dogquiz.common.dagger.modules

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import au.com.jayne.dogquiz.common.util.SharedPreferenceStorage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object StorageModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(applicationContext: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

    @Provides
    internal fun providesSharedPreferenceStorage(sharedPreferences: SharedPreferences): SharedPreferenceStorage {
        return SharedPreferenceStorage(sharedPreferences)
    }
}