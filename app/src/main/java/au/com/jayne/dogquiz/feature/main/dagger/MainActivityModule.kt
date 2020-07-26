package au.com.jayne.dogquiz.feature.main.dagger

import androidx.lifecycle.ViewModel
import au.com.jayne.dogquiz.common.dagger.viewmodel.ViewModelKey
import au.com.jayne.dogquiz.common.preference.YesNoPreferenceDialogFragment
import au.com.jayne.dogquiz.feature.game.GameFragment
import au.com.jayne.dogquiz.feature.game.GameViewModel
import au.com.jayne.dogquiz.feature.main.MainActivity
import au.com.jayne.dogquiz.feature.score.ScoresFragment
import au.com.jayne.dogquiz.feature.score.ScoresViewModel
import au.com.jayne.dogquiz.feature.selection.GameSelectionFragment
import au.com.jayne.dogquiz.feature.settings.SettingsFragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector
    internal abstract fun provideMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun provideGameSelectionFragment(): GameSelectionFragment

    @ContributesAndroidInjector
    abstract fun provideGameFragment(): GameFragment

    @ContributesAndroidInjector
    abstract fun provideScoresFragment(): ScoresFragment

    @ContributesAndroidInjector
    abstract fun provideYesNoPreferenceDialogFragment(): YesNoPreferenceDialogFragment

    @ContributesAndroidInjector
    abstract fun provideSettingsFragment(): SettingsFragment

    @Binds
    @IntoMap
    @ViewModelKey(GameViewModel::class)
    internal abstract fun bindGameViewModel(gameViewModel: GameViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ScoresViewModel::class)
    internal abstract fun bindScoresViewModel(scoresViewModel: ScoresViewModel): ViewModel
}