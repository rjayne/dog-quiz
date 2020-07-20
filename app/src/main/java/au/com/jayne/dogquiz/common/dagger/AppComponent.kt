package au.com.jayne.dogquiz.common.dagger

import au.com.jayne.dogquiz.DogQuizApplication
import au.com.jayne.dogquiz.common.dagger.modules.AppModule
import au.com.jayne.dogquiz.common.dagger.modules.ViewModelFactoryModule
import au.com.jayne.dogquiz.feature.main.dagger.MainActivityModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    ViewModelFactoryModule::class,
    MainActivityModule::class
])
interface AppComponent: AndroidInjector<DogQuizApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: DogQuizApplication): Builder
        fun build(): AppComponent
    }

}