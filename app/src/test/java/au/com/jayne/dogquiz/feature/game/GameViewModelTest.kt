package au.com.jayne.dogquiz.feature.game

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.test.core.app.ApplicationProvider
import au.com.jayne.dogquiz.BaseTest
import au.com.jayne.dogquiz.R
import au.com.jayne.dogquiz.common.network.ConnectionStateMonitor
import au.com.jayne.dogquiz.common.util.ResourceProvider
import au.com.jayne.dogquiz.common.util.SharedPreferenceStorage
import au.com.jayne.dogquiz.domain.model.*
import au.com.jayne.dogquiz.domain.repo.DogRepository
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowNetworkCapabilities

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
//@Config(sdk= [Build.VERSION_CODES.P])
class GameViewModelTest: BaseTest() {

    @Mock
    lateinit var mockDogRepository: DogRepository

    @Mock
    lateinit var mockContext: Context

    @Mock
    lateinit var mockConnectivityManager: ConnectivityManager

    @Mock
    lateinit var mockSharedPreferenceStorage: SharedPreferenceStorage

    @Mock
    lateinit var mockResourceProvider: ResourceProvider

    @Mock
    lateinit var mockRandomDogListGenerator: RandomDogListGenerator

    lateinit var connectionStateMonitor: ConnectionStateMonitor

    lateinit var viewModel: GameViewModel

    private var dogChallenge: DogChallenge? = null
    private var imagesToPreload: ArrayList<String>? = null
    private var score: Int? = null
    private var bonesLeft: Int? = null
    private var highScoreAchieved: Boolean? = null
    private var gameOver: Boolean? = null
    private var errorMessage: MessageDetails? = null

    private val challengeDogList = ArrayList<Dog>()

    private fun getGameScores(): GameScores {
        val highScoresMap = HashMap<Game, HighScore>()
        highScoresMap.put(Game.SPANIEL_QUIZ, HighScore("Spaniel Boy", 5))
        highScoresMap.put(Game.TERRIER_QUIZ, HighScore("Terrier Girl", 6))
        highScoresMap.put(Game.DOGGY_QUIZ, HighScore("Grrrreat Dog", 1))
        return GameScores(highScoresMap)
    }

    @Before
    fun setup() {
        dogChallenge = null
        imagesToPreload = null
        score = null
        bonesLeft = null
        highScoreAchieved = null
        gameOver = null
        errorMessage = null
        challengeDogList.clear()

        whenever(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager)
        connectionStateMonitor = ConnectionStateMonitor(mockContext)

        viewModel = GameViewModel(mockDogRepository, connectionStateMonitor, mockSharedPreferenceStorage, mockResourceProvider)
        viewModel.markAsTest()

        viewModel.dogChallenge.observeForever {
            dogChallenge = it
        }

        viewModel.imagesToPreload.observeForever {
            imagesToPreload = it
        }

        viewModel.score.observeForever {
            score = it
        }

        viewModel.bonesLeft.observeForever {
            bonesLeft = it
        }

        viewModel.highScoreAchieved.observeForever {
            highScoreAchieved = it?.consume()
        }

        viewModel.gameOver.observeForever {
            gameOver = it?.consume()
        }

        viewModel.errorMessage.observeForever {
            errorMessage = it?.consume()
        }
    }

    @Test
    fun testStartNewGame() {
        whenever(mockSharedPreferenceStorage.getGameScores()).thenReturn(getGameScores())

        viewModel.startNewGame(Game.DOGGY_QUIZ)

        assertValuesRestarted()
    }

    @Test
    fun testPlayGame() = runBlocking {
        setupGameForTest()

        viewModel.playGame()

        Assert.assertNotNull(dogChallenge)
        Assert.assertNotNull(imagesToPreload)
        Assert.assertEquals(10, imagesToPreload!!.size) // 10 Dog Challenges prepopulating

        // check clear game
        viewModel.clearGame()
        assertValuesRestarted()
    }

    @Test
    fun testOnDogSelected_GameOver() {
        whenever(mockSharedPreferenceStorage.getGameScores()).thenReturn(getGameScores())

        setupGameForTest()
        viewModel.startNewGame(Game.HOUND_QUIZ)
        viewModel.playGame()

        Assert.assertEquals(0, score)
        Assert.assertEquals(3, bonesLeft)

        viewModel.onDogSelected(Dog("husky")) // Incorrect answer
        Assert.assertEquals(2, bonesLeft)
        Assert.assertNull(gameOver)

        viewModel.onDogSelected(Dog("husky")) // Incorrect answer
        Assert.assertEquals(1, bonesLeft)
        Assert.assertNull(gameOver)

        viewModel.onDogSelected(Dog("husky")) // Incorrect answer
        Assert.assertEquals(0, bonesLeft)
        Assert.assertNotNull(gameOver)
        Assert.assertTrue(gameOver!!)
    }

    @Test
    fun testOnDogSelected_NullHighScore() {
        whenever(mockSharedPreferenceStorage.getGameScores()).thenReturn(getGameScores())
        viewModel.startNewGame(Game.HOUND_QUIZ)
        setupGameForTest()
        viewModel.playGame()

        Assert.assertEquals(0, score)
        Assert.assertEquals(3, bonesLeft)

        var correctDog = viewModel.dogChallenge.value!!.dog
        viewModel.onDogSelected(correctDog) // Correct answer

        Assert.assertEquals(1, score)
        Assert.assertEquals(3, bonesLeft)
        Assert.assertNull(gameOver)

        correctDog = viewModel.dogChallenge.value!!.dog
        viewModel.onDogSelected(correctDog) // Correct answer
        Assert.assertEquals(2, score)
        Assert.assertEquals(3, bonesLeft)
        Assert.assertNull(gameOver)

        viewModel.onDogSelected(Dog("husky")) // Incorrect answer
        Assert.assertEquals(2, bonesLeft)
        Assert.assertNull(gameOver)

        viewModel.onDogSelected(Dog("husky")) // Incorrect answer
        Assert.assertEquals(1, bonesLeft)
        Assert.assertNull(gameOver)

        viewModel.onDogSelected(Dog("husky")) // Incorrect answer
        Assert.assertEquals(0, bonesLeft)
        Assert.assertNotNull(highScoreAchieved)
        Assert.assertTrue(highScoreAchieved!!)

        Assert.assertNull(gameOver)
    }

    @Test
    fun testOnDogSelected_BeatsHighScore() {
        whenever(mockSharedPreferenceStorage.getGameScores()).thenReturn(getGameScores())
        viewModel.startNewGame(Game.DOGGY_QUIZ)
        setupGameForTest()
        viewModel.playGame()

        Assert.assertEquals(0, score)
        Assert.assertEquals(3, bonesLeft)

        var correctDog = viewModel.dogChallenge.value!!.dog
        viewModel.onDogSelected(correctDog) // Correct answer

        Assert.assertEquals(1, score)
        Assert.assertEquals(3, bonesLeft)
        Assert.assertNull(gameOver)

        correctDog = viewModel.dogChallenge.value!!.dog
        viewModel.onDogSelected(correctDog) // Correct answer
        Assert.assertEquals(2, score)
        Assert.assertEquals(3, bonesLeft)
        Assert.assertNull(gameOver)

        viewModel.onDogSelected(Dog("husky")) // Incorrect answer
        Assert.assertEquals(2, bonesLeft)
        Assert.assertNull(gameOver)

        viewModel.onDogSelected(Dog("husky")) // Incorrect answer
        Assert.assertEquals(1, bonesLeft)
        Assert.assertNull(gameOver)

        viewModel.onDogSelected(Dog("husky")) // Incorrect answer
        Assert.assertEquals(0, bonesLeft)
        Assert.assertNotNull(highScoreAchieved)
        Assert.assertTrue(highScoreAchieved!!)

        Assert.assertNull(gameOver)
    }

    @Test
    fun testRecordHighScore_Anonymous(){
        val anonymous = "Anonymous"
        viewModel.startNewGame(Game.HOUND_QUIZ)

        whenever(mockResourceProvider.getString(R.string.unknown_player)).thenReturn(anonymous)

        viewModel.recordHighScore()

        verify(mockSharedPreferenceStorage, times(1)).setGameScore(Game.HOUND_QUIZ, HighScore(anonymous, 0))
    }

    @Test
    fun testRecordHighScore(){
        val name = "Joe"
        viewModel.startNewGame(Game.HOUND_QUIZ)

        viewModel.recordHighScore(name)

        verify(mockSharedPreferenceStorage, times(1)).setGameScore(Game.HOUND_QUIZ, HighScore(name, 0))
        verifyZeroInteractions(mockResourceProvider)
    }

    @Test
    fun testGetRandomDogListGenerator() {
        viewModel.startNewGame(Game.DOGGY_QUIZ)
        Assert.assertTrue(viewModel.randomDogListGenerator is DoggyDogGenerator)

        viewModel.startNewGame(Game.SPANIEL_QUIZ)
        Assert.assertTrue(viewModel.randomDogListGenerator is DogInBreedGenerator)

        viewModel.startNewGame(Game.HOUND_QUIZ)
        Assert.assertTrue(viewModel.randomDogListGenerator is DogInBreedGenerator)

        viewModel.startNewGame(Game.TERRIER_QUIZ)
        Assert.assertTrue(viewModel.randomDogListGenerator is DogInBreedGenerator)
    }

    private fun setupGameForTest() = runBlocking {
        val breed = "hound"
        val allDogList = ArrayList<Dog>()
        allDogList.add(Dog(breed, "afghan"))
        allDogList.add(Dog(breed, "basset"))
        allDogList.add(Dog(breed, "blood"))
        allDogList.add(Dog(breed, "english"))
        allDogList.add(Dog(breed, "ibizan"))
        allDogList.add(Dog(breed, "plott"))
        allDogList.add(Dog(breed, "walker"))

        challengeDogList.clear()
        challengeDogList.add(Dog(breed, "ibizan"))
        challengeDogList.add(Dog(breed, "plott"))
        challengeDogList.add(Dog(breed, "walker"))

        whenever(mockRandomDogListGenerator.getDogList()).thenReturn(allDogList)
        whenever(mockRandomDogListGenerator.getRandomDogsWithExclusion(any())).thenReturn(challengeDogList)

        whenever(mockDogRepository.getRandomImage(any())).thenReturn("https://someurl.com/image")

        viewModel.ioDispatcher = coroutinesTestRule.testDispatcher
        viewModel.randomDogListGenerator = mockRandomDogListGenerator

        Assert.assertNull(dogChallenge)
    }

    private fun assertValuesRestarted() {
        Assert.assertNull(highScoreAchieved)
        Assert.assertNull(gameOver)
        Assert.assertNull(dogChallenge)
        Assert.assertEquals(0, score)
        Assert.assertEquals(3, bonesLeft)
        Assert.assertNull(errorMessage)
        Assert.assertEquals(0, imagesToPreload?.size)
    }
}