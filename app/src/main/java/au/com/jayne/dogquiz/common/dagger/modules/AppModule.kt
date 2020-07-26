package au.com.jayne.dogquiz.common.dagger.modules

import android.app.Application
import android.content.Context
import au.com.jayne.dogquiz.DogQuizApplication
import au.com.jayne.dogquiz.common.util.ResourceProvider
import au.com.jayne.dogquiz.common.util.ResourceProviderImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    internal fun providesApplication(application: DogQuizApplication): Application {
        return application
    }

    @Provides
    @Singleton
    internal fun providesApplicationContext(application: DogQuizApplication): Context {
        return application.applicationContext
    }

    @Provides
    internal fun providesResourceProvider(context: Context): ResourceProvider {
        return ResourceProviderImpl(context)
    }
}