package au.com.jayne.dogquiz.feature.main.dagger

import au.com.jayne.dogquiz.feature.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector
    internal abstract fun provideMainActivity(): MainActivity

}