package au.com.jayne.dogquiz.common.dagger.modules

import androidx.lifecycle.ViewModelProvider
import au.com.jayne.dogquiz.common.dagger.viewmodel.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}