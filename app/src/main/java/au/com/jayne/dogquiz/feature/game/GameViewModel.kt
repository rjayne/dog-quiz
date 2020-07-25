package au.com.jayne.dogquiz.feature.game

import android.media.SoundPool
import android.text.InputType
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.com.jayne.dogquiz.R
import au.com.jayne.dogquiz.common.network.ConnectionStateMonitor
import au.com.jayne.dogquiz.common.util.ResourceProvider
import au.com.jayne.dogquiz.common.util.SharedPreferenceStorage
import au.com.jayne.dogquiz.domain.exception.QuizException
import au.com.jayne.dogquiz.domain.model.*
import au.com.jayne.dogquiz.domain.repo.DogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


class GameViewModel  @Inject constructor(private val dogRepository: DogRepository, private val connectionStateMonitor: ConnectionStateMonitor, private val sharedPreferenceStorage: SharedPreferenceStorage, private val resourceProvider: ResourceProvider): ViewModel(){

    private var game = Game.DOGGY_QUIZ // default to Doggy Quiz

    private val _dogChallenge = MutableLiveData<DogChallenge>()
    val dogChallenge: LiveData<DogChallenge>
        get() = _dogChallenge

    val imagesToPreload = MutableLiveData<ArrayList<String>>()

    private val _score = MutableLiveData<Int>(0)
    val score: LiveData<Int>
        get() = _score

    private val _bonesLeft = MutableLiveData<Int>(0)
    val bonesLeft: LiveData<Int>
        get() = _bonesLeft

    private val _highScoreAchieved = MutableLiveData<Boolean>(false)
    val highScoreAchieved: LiveData<Boolean>
        get() = _highScoreAchieved

    internal val _errorMessage = MutableLiveData<Event<MessageDetails>>()
    val errorMessage: LiveData<Event<MessageDetails>>
        get() = _errorMessage

    var ioDispatcher = Dispatchers.IO

    // Cache the different RandomDogListGenerators, so that the base data doesn't need to be
    // repopulated if the user returns to the game after playing a different game. This is just for
    // speed of loading.
    private val spanielGenerator: DogInBreedGenerator by lazy { DogInBreedGenerator(Game.SPANIEL_QUIZ.breed!!, dogRepository) }
    private val houndGenerator: DogInBreedGenerator by lazy { DogInBreedGenerator(Game.HOUND_QUIZ.breed!!, dogRepository) }
    private val terrierGenerator: DogInBreedGenerator by lazy { DogInBreedGenerator(Game.TERRIER_QUIZ.breed!!, dogRepository) }
    private val doggyDogGenerator: DoggyDogGenerator by lazy { DoggyDogGenerator(dogRepository) }

    // Lazy load error messages
    private val unexpectedErrorMessageTitle = R.string.error_unexpected_title
    private val unexpectedErrorMessage by lazy {
        MessageDetails(QuizMessageCode.UNKNOWN, MessageContainer(R.string.error_unexpected_message, unexpectedErrorMessageTitle))
    }

    private var randomDogListGenerator: RandomDogListGenerator? = null
    private val dogsChallenges = ArrayList<DogChallenge>()

    private var failureDueToNoInternet = false

    internal var soundPool: SoundPool? = null
    internal var successSoundId: Int? = null
    internal var failureSoundId: Int? = null

    private var highScore: HighScore? = null

    fun startNewGame(game: Game) {
        Timber.d("startNewGame - $game")
        this.game = game
        clearGame()

        randomDogListGenerator = getRandomDogListGenerator()

        highScore = sharedPreferenceStorage.getGameScores()?.highScoresMap?.get(game)
    }

    fun playGame() {
        Timber.d("playGame")
        viewModelScope.launch(ioDispatcher) {
            populateDogChallenges()
        }
    }

    fun clearGame() {
        _highScoreAchieved.value = false
        _dogChallenge.value = null
        _score.value = 0
        _bonesLeft.value = 3
        _errorMessage.value = null
        imagesToPreload.value = ArrayList()
        dogsChallenges.clear()
    }

    fun onDogSelected(dog: Dog) {
        if(dog.equals(dogChallenge.value?.dog)) {
            scoreAchieved()
            displayNextChallenge()
        } else {
            boneLost()
        }
    }

    private fun scoreAchieved() {
        successSoundId?.let{
            playSound(it)
        }
        _score.value = _score.value?.plus(1)
    }

    private fun displayNextChallenge() {
        if(dogsChallenges.isNotEmpty()) {
            _dogChallenge.value = dogsChallenges.removeAt(0)
        } else {
            _dogChallenge.value = null
        }
        populateDogChallenges()
    }

    private fun boneLost() {
        failureSoundId?.let{
            playSound(it)
        }
        _bonesLeft.value = _bonesLeft.value?.minus(1)

        if(_bonesLeft.value == 0) {
            gameOverCheckForHighScore()
        }
    }

    fun gameOverCheckForHighScore(): Boolean {
        Timber.d("GAME OVER")
        if(highScore == null) {
            _highScoreAchieved.value = true
            return true
        }
        highScore?.let{ highScore ->
            if(highScore.score < _score.value!!) {
                _highScoreAchieved.value = true
                return true
            }
        }

        return false
    }

    fun recordHighScore(playerName: String? = null) {
        var name = playerName
        if(playerName.isNullOrBlank()) {
            name = resourceProvider.getString(R.string.unknown_player)
        }
        sharedPreferenceStorage.setGameScore(game, HighScore(name!!, _score.value!!))
    }

    private fun playSound(soundId: Int) {
        if(sharedPreferenceStorage.isSoundEnabled()) {
            soundPool?.play(soundId, 1f, 1f, 0, 0, 1f)
        }
    }

    private fun getRandomDogListGenerator(): RandomDogListGenerator {
        when(game) {
            Game.SPANIEL_QUIZ -> {
                return spanielGenerator
            }
            Game.HOUND_QUIZ -> {
                return houndGenerator
            }
            Game.TERRIER_QUIZ -> {
                return terrierGenerator
            }
            else -> {
                return doggyDogGenerator
            }
        }
    }

    private fun populateDogChallenges() {
        viewModelScope.launch(ioDispatcher) {
            Timber.d("populateDogChallenges - dogsChallenges.size: ${dogsChallenges.size}")
            while (dogsChallenges.size < MIN_CHALLENGES_POPULATED) {
                val dogChallenge = getDogChallenge()
                if (dogChallenge == null) break // If error occurred, do not try again

                viewModelScope.launch(Dispatchers.Main) {
                    if (_dogChallenge.value == null) {
                        _dogChallenge.value = dogChallenge // first dog in the Quiz
                    } else {
                        dogsChallenges.add(dogChallenge)
                        val list = imagesToPreload.value
                        list?.add(dogChallenge.imageUrl)
                        Timber.d("populateDogsWithImages - add dog challenge and update images to preload - ${list?.size}")
                        imagesToPreload.value = list
                    }
                }
            }
        }
    }

    private suspend fun getDogChallenge(): DogChallenge? {
        randomDogListGenerator?.let{ randomDogListGenerator ->

            val dog = makeNetworkCall<List<Dog>>(randomDogListGenerator::getDogList)?.shuffled()?.first()
            dog?.let{
                val dogSelection = ArrayList<Dog>()
                dogSelection.add(dog)
                dogSelection.addAll(randomDogListGenerator.getRandomDogsWithExclusion(dog))

                val randomImageUrl = makeNetworkCall<String>({dogRepository.getRandomImage(dog)})?: ""
                randomImageUrl?.let{
                    return DogChallenge(dog, randomImageUrl, dogSelection.shuffled())
                }
            }
        }

        return null
    }

    private suspend fun <T> makeNetworkCall(call: suspend() -> T): T? {
        try {
            if(checkInternetConnection()) {
                return call()
            }
        } catch (ex: QuizException) {
            Timber.d("QuizException caught - ${ex.messageDetails}")
            viewModelScope.launch(Dispatchers.Main) {
                if (connectionStateMonitor.hasInternetConnection()) {
                    displayError(ex.messageDetails)
                } else {
                    failureDueToNoInternet = true
                    displayNoInternetMessage(ex.messageDetails.messageCode)
                }
            }
        } catch (throwable: Throwable) {
            Timber.i(
                throwable,
                "makeNetworkCall - Unable to find dogs or dog image)"
            )
            viewModelScope.launch(Dispatchers.Main) {
                if (connectionStateMonitor.hasInternetConnection()) {
                    displayError(unexpectedErrorMessage)
                } else {
                    failureDueToNoInternet = true
                    displayNoInternetMessage(QuizMessageCode.NO_INTERNET_CONNECTION)
                }
            }
        }

        return null
    }

    fun retryIfInternetFailedPreviously() {
        Timber.d("retryIfInternetFailedPreviously - failureDueToNoInternet: $failureDueToNoInternet")
        if(failureDueToNoInternet) {
            failureDueToNoInternet = false
            if(dogsChallenges.size < MIN_CHALLENGES_POPULATED) {
                Timber.d("Retry loading due to previoiusly failed internet. dogsChallenges.size = ${dogsChallenges.size}")
                viewModelScope.launch(ioDispatcher) {
                    populateDogChallenges()
                }
            }
        }
    }

    fun checkInternetConnection(): Boolean {
        if(!connectionStateMonitor.hasInternetConnection()) {
            val code = if(_dogChallenge.value == null) QuizMessageCode.ALL_DOGS_LOOKUP_FAILURE else QuizMessageCode.NO_INTERNET_CONNECTION
            failureDueToNoInternet = true
            displayNoInternetMessage(code)
            return false
        }

        return true
    }

    private fun displayError(errorMessage: MessageDetails) {
        viewModelScope.launch(Dispatchers.Main) {
            _errorMessage.value = null
            _errorMessage.value = Event(errorMessage)
        }
    }

    private fun displayNoInternetMessage(messageCode: QuizMessageCode) {
        displayError(getNoInternetErrorMessage(messageCode))
    }

    private fun getNoInternetErrorMessage(messageCode: QuizMessageCode): MessageDetails {
        return MessageDetails(messageCode, MessageContainer(R.string.error_no_internet_message, R.string.error_no_internet_title))
    }

    companion object {
        val MIN_CHALLENGES_POPULATED = 10
        val ANONYMOUS = "Anonymous"
    }
}