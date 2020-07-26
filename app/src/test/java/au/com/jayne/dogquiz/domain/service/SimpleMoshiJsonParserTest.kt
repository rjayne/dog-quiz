package au.com.jayne.dogquiz.domain.service

import au.com.jayne.dogquiz.domain.model.Game
import au.com.jayne.dogquiz.domain.model.GameScores
import au.com.jayne.dogquiz.domain.model.HighScore
import org.junit.Assert
import org.junit.Test

class SimpleMoshiJsonParserTest {

    @Test
    fun testToFromJson_GameScores() {
        val highScoresMap = HashMap<Game, HighScore>()
        highScoresMap.put(Game.SPANIEL_QUIZ, HighScore("Spaniel Boy", 5))
        val gameScores = GameScores(highScoresMap)

        val json = "{\"highScoresMap\":{" +
                            "\"SPANIEL_QUIZ\":{\"name\":\"Spaniel Boy\",\"score\":5}" +
                        "}"+
                    "}"

        Assert.assertEquals(json, SimpleMoshiJsonParser.toJson(gameScores, GameScores::class.java))
        Assert.assertEquals(gameScores, SimpleMoshiJsonParser.fromJson(json, GameScores::class.java))
    }

    @Test
    fun testToFromJson_GameScores_All() {
        val highScoresMap = HashMap<Game, HighScore>()
        highScoresMap.put(Game.SPANIEL_QUIZ, HighScore("Spaniel Boy", 5))
        highScoresMap.put(Game.HOUND_QUIZ, HighScore("Hounder", 10))
        highScoresMap.put(Game.TERRIER_QUIZ, HighScore("Terrier Girl", 6))
        highScoresMap.put(Game.DOGGY_QUIZ, HighScore("Grrrreat Dog", 15))
        val gameScores = GameScores(highScoresMap)

        val json = "{\"highScoresMap\":{" +
                "\"SPANIEL_QUIZ\":{\"name\":\"Spaniel Boy\",\"score\":5}," +
                "\"HOUND_QUIZ\":{\"name\":\"Hounder\",\"score\":10}," +
                "\"TERRIER_QUIZ\":{\"name\":\"Terrier Girl\",\"score\":6}," +
                "\"DOGGY_QUIZ\":{\"name\":\"Grrrreat Dog\",\"score\":15}" +
                "}"+
                "}"

        // Can't test toJson, as the highScoresMap values returned can be in any order, so the
        // result is different each time
        Assert.assertEquals(gameScores, SimpleMoshiJsonParser.fromJson(json, GameScores::class.java))
    }

}