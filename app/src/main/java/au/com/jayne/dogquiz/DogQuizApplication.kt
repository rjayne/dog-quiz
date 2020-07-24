package au.com.jayne.dogquiz

import au.com.jayne.dogquiz.common.dagger.DaggerAppComponent
import au.com.jayne.dogquiz.common.util.ApplicationCacheHandler
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber

class DogQuizApplication: DaggerApplication() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("onCreate - timber setup")
        }

        ApplicationCacheHandler.initializeApplication(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build()
    }
}