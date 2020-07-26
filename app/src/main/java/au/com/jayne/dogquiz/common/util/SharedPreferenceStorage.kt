package au.com.jayne.dogquiz.common.util

import au.com.jayne.dogquiz.domain.model.Game
import au.com.jayne.dogquiz.domain.model.GameScores
import au.com.jayne.dogquiz.domain.model.HighScore

interface SharedPreferenceStorage {

    fun isSoundEnabled(): Boolean

    fun resetHighScores()

    fun setGameScore(game: Game, highScore: HighScore)

    fun getGameScores(): GameScores?

    fun getLastNameUsed(): String?

    fun setLastNameUsed(name: String)

}