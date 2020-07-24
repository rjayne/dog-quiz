package au.com.jayne.dogquiz.feature.score

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.jayne.dogquiz.R
import au.com.jayne.dogquiz.common.util.ResourceProvider
import au.com.jayne.dogquiz.common.util.SharedPreferenceStorage
import au.com.jayne.dogquiz.domain.model.Game
import au.com.jayne.dogquiz.domain.model.GameScores
import au.com.jayne.dogquiz.domain.model.HighScore
import javax.inject.Inject

class ScoresViewModel @Inject constructor(private val sharedPreferenceStorage: SharedPreferenceStorage, private val resourceProvider: ResourceProvider): ViewModel() {

    private val _spanielHighScore = MutableLiveData<String>()
    val spanielHighScore: LiveData<String>
        get() = _spanielHighScore

    private val _houndHighScore = MutableLiveData<String>()
    val houndHighScore: LiveData<String>
        get() = _houndHighScore

    private val _terrierHighScore = MutableLiveData<String>()
    val terrierHighScore: LiveData<String>
        get() = _terrierHighScore

    private val _doggyHighScore = MutableLiveData<String>()
    val doggyHighScore: LiveData<String>
        get() = _doggyHighScore

    fun refreshHighScores() {
        var gameScores = sharedPreferenceStorage.getGameScores()
        if(gameScores == null) {
            gameScores = GameScores(HashMap<Game, HighScore>())
        }

        _spanielHighScore.value = getDisplayString(getHighScore(gameScores, Game.SPANIEL_QUIZ))
        _houndHighScore.value = getDisplayString(getHighScore(gameScores, Game.HOUND_QUIZ))
        _terrierHighScore.value = getDisplayString(getHighScore(gameScores, Game.TERRIER_QUIZ))
        _doggyHighScore.value = getDisplayString(getHighScore(gameScores, Game.DOGGY_QUIZ))
    }

    private fun getDisplayString(highScore: HighScore?): String {
        highScore?.let{
            return "${highScore.name}: ${highScore.score}"
        }
        return resourceProvider.getString(R.string.high_score_not_played)
    }

    private fun getHighScore(gameScores: GameScores, game: Game): HighScore? {
        return gameScores.highScoresMap.get(game)
    }

}