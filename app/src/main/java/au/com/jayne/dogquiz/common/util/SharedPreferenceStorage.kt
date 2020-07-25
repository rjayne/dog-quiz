package au.com.jayne.dogquiz.common.util

import android.content.SharedPreferences
import au.com.jayne.dogquiz.domain.model.Game
import au.com.jayne.dogquiz.domain.model.GameScores
import au.com.jayne.dogquiz.domain.model.HighScore
import au.com.jayne.dogquiz.domain.model.SharedPreferenceKey
import au.com.jayne.dogquiz.domain.service.SimpleMoshiJsonParser
import timber.log.Timber
import javax.inject.Inject

class SharedPreferenceStorage @Inject constructor(sharedPreferences: SharedPreferences){

    private val sharedPreferenceAccessor: SharedPreferenceAccessor = SharedPreferenceAccessor(sharedPreferences)

    fun isSoundEnabled(): Boolean {
        return sharedPreferenceAccessor.getBooleanFromPreferences(SharedPreferenceKey.SOUNDS_ENABLED, true)
    }

    fun resetHighScores() {
        Timber.d("resetHighScores")
        sharedPreferenceAccessor.clearValue(SharedPreferenceKey.GAME_SCORES)
    }

    fun setGameScore(game: Game, highScore: HighScore) {
        var gameScores: GameScores? = getGameScores()
        if(gameScores == null) {
            val highScores = HashMap<Game, HighScore>()
            highScores.put(game, highScore)
            gameScores = GameScores(highScores)
        } else {
            val highScores = HashMap<Game, HighScore>()
            highScores.putAll(gameScores.highScoresMap)
            highScores.put(game, highScore)
            gameScores = GameScores(highScores)
        }

        setGameScores(gameScores)
        setLastNameUsed(highScore.name)
    }

    private fun setGameScores(gameScores: GameScores) {
        val gameScoresJson = SimpleMoshiJsonParser.toJson(gameScores, GameScores::class.java)
        sharedPreferenceAccessor.editSharedPreferences(SharedPreferenceKey.GAME_SCORES, gameScoresJson)
        Timber.d("setGameScores - $gameScoresJson")
    }

    fun getGameScores(): GameScores? {
        sharedPreferenceAccessor.getStringFromPreferences(SharedPreferenceKey.GAME_SCORES)?.let {
            Timber.d("getGameScores - ${SimpleMoshiJsonParser.fromJson(it, GameScores::class.java)}")
            return SimpleMoshiJsonParser.fromJson(it, GameScores::class.java)
        }

        return null
    }

    fun getLastNameUsed(): String? {
        return sharedPreferenceAccessor.getStringFromPreferences(SharedPreferenceKey.LAST_NAME_USED)
    }

    fun setLastNameUsed(name: String) {
        sharedPreferenceAccessor.editSharedPreferences(SharedPreferenceKey.LAST_NAME_USED, name)
    }
}