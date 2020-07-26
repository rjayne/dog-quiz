package au.com.jayne.dogquiz.feature.score

import au.com.jayne.dogquiz.BaseTest
import au.com.jayne.dogquiz.R
import au.com.jayne.dogquiz.common.util.ResourceProvider
import au.com.jayne.dogquiz.common.util.SharedPreferenceStorage
import au.com.jayne.dogquiz.domain.model.Game
import au.com.jayne.dogquiz.domain.model.GameScores
import au.com.jayne.dogquiz.domain.model.HighScore
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

class ScoresViewModelTest: BaseTest() {

    @Mock
    lateinit var mockSharedPreferenceStorage: SharedPreferenceStorage

    @Mock
    lateinit var mockResourceProvider: ResourceProvider

    lateinit var viewModel: ScoresViewModel

    private var spanielHighScore: String? = null
    private var houndHighScore: String? = null
    private var terrierHighScore: String? = null
    private var doggyHighScore: String? = null

    private val noScore = "---"

    @Before
    fun setup() {
        viewModel = ScoresViewModel(mockSharedPreferenceStorage, mockResourceProvider)

        viewModel.spanielHighScore.observeForever{
            spanielHighScore = it
        }

        viewModel.houndHighScore.observeForever{
            houndHighScore = it
        }

        viewModel.terrierHighScore.observeForever{
            terrierHighScore = it
        }

        viewModel.doggyHighScore.observeForever{
            doggyHighScore = it
        }
    }

    @Test
    fun testRefreshHighScores_noScores() {
        whenever(mockSharedPreferenceStorage.getGameScores()).thenReturn(null)
        whenever(mockResourceProvider.getString(R.string.high_score_not_played)).thenReturn(noScore)
        viewModel.refreshHighScores()

        Assert.assertEquals(noScore, spanielHighScore)
        Assert.assertEquals(noScore, houndHighScore)
        Assert.assertEquals(noScore, terrierHighScore)
        Assert.assertEquals(noScore, doggyHighScore)
    }

    @Test
    fun testRefreshHighScores() {
        val highScoresMap = HashMap<Game, HighScore>()
        highScoresMap.put(Game.SPANIEL_QUIZ, HighScore("Spaniel Boy", 5))
        highScoresMap.put(Game.HOUND_QUIZ, HighScore("Hounder", 10))
        highScoresMap.put(Game.TERRIER_QUIZ, HighScore("Terrier Girl", 6))
        highScoresMap.put(Game.DOGGY_QUIZ, HighScore("Grrrreat Dog", 15))
        val gameScores = GameScores(highScoresMap)

        whenever(mockSharedPreferenceStorage.getGameScores()).thenReturn(gameScores)
        viewModel.refreshHighScores()

        Assert.assertEquals("Spaniel Boy: 5", spanielHighScore)
        Assert.assertEquals("Hounder: 10", houndHighScore)
        Assert.assertEquals("Terrier Girl: 6", terrierHighScore)
        Assert.assertEquals("Grrrreat Dog: 15", doggyHighScore)

        verify(mockResourceProvider, times(0)).getString(R.string.high_score_not_played)
    }
}