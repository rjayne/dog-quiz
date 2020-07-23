package au.com.jayne.dogquiz.feature.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.com.jayne.dogquiz.R
import au.com.jayne.dogquiz.common.network.ConnectionStateMonitor
import au.com.jayne.dogquiz.common.util.ResourceProvider
import au.com.jayne.dogquiz.domain.exception.QuizException
import au.com.jayne.dogquiz.domain.model.*
import au.com.jayne.dogquiz.domain.repo.DogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class GameViewModel  @Inject constructor(private val dogRepository: DogRepository, private val connectionStateMonitor: ConnectionStateMonitor, val resourceProvider: ResourceProvider): ViewModel(){

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

    internal val _errorMessage = MutableLiveData<Event<MessageContainer>>()
    val errorMessage: LiveData<Event<MessageContainer>>
        get() = _errorMessage

    var ioDispatcher = Dispatchers.IO

    private val unexpectedErrorMessageTitle by lazy { resourceProvider.getString(R.string.error_unexpected_title) }
    private val unexpectedErrorMessage by lazy {
        MessageContainer(unexpectedErrorMessageTitle, resourceProvider.getString(R.string.error_unexpected_message))
    }
    private val noInternetErrorMessage by lazy {
        MessageContainer(resourceProvider.getString(R.string.error_no_internet_title), resourceProvider.getString(R.string.error_no_internet_message))
    }

    private lateinit var randomDogListGenerator: RandomDogListGenerator
    private val dogsChallenges = ArrayList<DogChallenge>()

    private var failureDueToNoInternet = false

    fun startGame(game: Game) {
        this.game = game
        _score.value = 0
        _bonesLeft.value = 3
        imagesToPreload.value = ArrayList()

        randomDogListGenerator = getRandomDogListGenerator()

        viewModelScope.launch(ioDispatcher) {
            initGame()
        }
    }

    fun onDogSelected(dog: Dog) {
        if(dog.equals(dogChallenge.value?.dog)) {
            Timber.d("Winner")
            _score.value = _score.value?.plus(1)
            _dogChallenge.value = dogsChallenges.removeAt(0)
            populateDogsWithImages()
        } else {
            Timber.d("Wrong choice")
            _bonesLeft.value = _bonesLeft.value?.minus(1)
            if(_bonesLeft.value == 0) {
                //TODO fail
                Timber.d("GAME OVER")
            }
        }
    }

    private fun getRandomDogListGenerator(): RandomDogListGenerator {
        if(game.subBreed != null) {
            return DogInBreedGenerator(game.subBreed!!, dogRepository)
        } else {
            return DoggyDogGenerator(dogRepository)
        }
    }

    private suspend fun initGame() {
        clearErrorMessage()
        lookupDogs()
        populateDogsWithImages()
    }

    private suspend fun lookupDogs() {
        makeNetworkCall<List<Dog>>(randomDogListGenerator::getDogList)
    }

    private fun populateDogsWithImages() {
        viewModelScope.launch(ioDispatcher) {
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
        val dog = makeNetworkCall<List<Dog>>(randomDogListGenerator::getDogList)?.shuffled()?.first()
        dog?.let{
            val dogSelection = ArrayList<Dog>()
            dogSelection.add(dog)
            dogSelection.addAll(randomDogListGenerator.getRandomDogsWithExclusion(dog))

            return DogChallenge(dog, dogRepository.getRandomImage(dog), dogSelection.shuffled())
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
                    displayError(getErrorMessage(ex.messageDetails))
                } else {
                    failureDueToNoInternet = true
                    displayNoInternetMessage()
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
                    displayNoInternetMessage()
                }
            }
        }

        return null
    }

    fun retryIfInternetFailedPreviously() {
        if(failureDueToNoInternet) {
            failureDueToNoInternet = false
            if(dogsChallenges.size < MIN_CHALLENGES_POPULATED) {
                viewModelScope.launch(ioDispatcher) {
                    initGame()
                }
            }
        }
    }

    private fun checkInternetConnection(): Boolean {
        if(!connectionStateMonitor.hasInternetConnection()) {
            displayNoInternetMessage()
            return false
        }

        return true
    }

    private fun displayError(errorMessage: MessageContainer) {
        _errorMessage.value = Event(errorMessage)
    }

    private fun clearErrorMessage() {
        if(_errorMessage.value != null) {
            _errorMessage.value = null
        }
    }

    private fun displayNoInternetMessage() {
        displayError(noInternetErrorMessage)
    }

    private fun getErrorMessage(messageDetails: MessageDetails): MessageContainer {
        messageDetails.message.messageResourceId?.let{
            return MessageContainer(unexpectedErrorMessageTitle, resourceProvider.getString(it))
        }

        messageDetails.message.messageText?.let{
            return MessageContainer(unexpectedErrorMessageTitle, it)
        }

        return unexpectedErrorMessage
    }

    fun update() {
        _score.value = _score.value?.plus(1)
        _bonesLeft.value = _bonesLeft.value?.minus(1)
    }

    companion object {
        val MIN_CHALLENGES_POPULATED = 10
    }
}