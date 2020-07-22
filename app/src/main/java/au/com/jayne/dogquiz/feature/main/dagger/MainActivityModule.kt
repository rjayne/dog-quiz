package au.com.jayne.dogquiz.feature.main.dagger

import au.com.jayne.dogquiz.common.preference.YesNoPreferenceDialogFragment
import au.com.jayne.dogquiz.feature.game.GameFragment
import au.com.jayne.dogquiz.feature.main.MainActivity
import au.com.jayne.dogquiz.feature.selection.GameSelectionFragment
import au.com.jayne.dogquiz.feature.settings.SettingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector
    internal abstract fun provideMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun provideGameSelectionFragment(): GameSelectionFragment

    @ContributesAndroidInjector
    abstract fun provideGamenFragment(): GameFragment

    @ContributesAndroidInjector
    abstract fun provideYesNoPreferenceDialogFragment(): YesNoPreferenceDialogFragment

    @ContributesAndroidInjector
    abstract fun provideSettingsFragment(): SettingsFragment
}