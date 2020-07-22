package au.com.jayne.dogquiz.feature.main

import androidx.lifecycle.ViewModel
import au.com.jayne.dogquiz.domain.model.Game
import javax.inject.Inject

class MainViewModel @Inject constructor(): ViewModel(){

    var gameSelected = Game.DOGGY_QUIZ // default to Doggy Quiz



}