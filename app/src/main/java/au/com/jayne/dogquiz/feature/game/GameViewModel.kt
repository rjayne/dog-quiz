package au.com.jayne.dogquiz.feature.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.jayne.dogquiz.R
import au.com.jayne.dogquiz.domain.model.Game
import javax.inject.Inject

class GameViewModel  @Inject constructor(): ViewModel(){

    var gameSelected = Game.DOGGY_QUIZ // default to Doggy Quiz

    private val _score = MutableLiveData<Int>(0)
    val score: LiveData<Int>
        get() = _score

    private val _bonesLeft = MutableLiveData<Int>(0)
    val bonesLeft: LiveData<Int>
        get() = _bonesLeft

    fun startGame() {
        _score.value = 0
        _bonesLeft.value = 3
    }

    fun update() {
        _score.value = _score.value?.plus(1)
        _bonesLeft.value = _bonesLeft.value?.minus(1)
    }
}