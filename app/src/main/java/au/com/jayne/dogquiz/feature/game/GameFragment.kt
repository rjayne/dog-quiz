package au.com.jayne.dogquiz.feature.game

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import au.com.jayne.dogquiz.R
import au.com.jayne.dogquiz.common.extensions.displayDialog
import au.com.jayne.dogquiz.common.extensions.getPositiveOkButtonDialog
import au.com.jayne.dogquiz.common.network.ConnectionStateMonitor
import au.com.jayne.dogquiz.common.ui.DialogFragmentCreator
import au.com.jayne.dogquiz.common.ui.DialogTargetFragment
import au.com.jayne.dogquiz.common.ui.EditTextDialogFragment
import au.com.jayne.dogquiz.databinding.GameFragmentBinding
import au.com.jayne.dogquiz.domain.model.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dagger.android.support.DaggerFragment
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import timber.log.Timber
import javax.inject.Inject


class GameFragment: DaggerFragment(), DialogTargetFragment {

    @Inject
    lateinit var connectionStateMonitor: ConnectionStateMonitor

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: GameFragmentBinding

    private val viewModel: GameViewModel by lazy { ViewModelProvider(requireActivity(), viewModelFactory).get(
        GameViewModel::class.java) }

    val navArgs: GameFragmentArgs by navArgs()

    private var messageDialogFragment: DialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")

        viewModel.startNewGame(navArgs.game)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = GameFragmentBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@GameFragment)
            if (viewModel?.dogChallenge?.value == null) {
                contentLoadingProgressBar.setVisibility(View.VISIBLE)
            }

            viewModel = this@GameFragment.viewModel

            viewModel?.score?.observe(viewLifecycleOwner, Observer<Int> { userScore ->
                score.text = userScore.toString()
            })
        }

        viewModel?.bonesLeft?.observe(viewLifecycleOwner, Observer<Int> { bonesLeft ->
            adjustBonesLeftImages(binding, bonesLeft)
        })

        viewModel?.dogChallenge?.observe(viewLifecycleOwner, Observer { dogChallenge ->
            dogChallenge?.let{
                loadDogChallengeImage(dogChallenge)
            }
        })

        viewModel?.imagesToPreload?.observe(viewLifecycleOwner, Observer { imagesToPreload ->
            if(viewModel?.checkInternetConnection() == true) {
                preloadDogChallengeImages(imagesToPreload)
            }
        })

        viewModel?.highScoreAchieved?.observe(viewLifecycleOwner, Observer { highScoreAchievedEvent ->
            val highScoreAchieved = highScoreAchievedEvent?.consume()
            if(highScoreAchieved == true) {
                displayConfetti()
                displayHighScoreDialog()
            }
        })

        viewModel?.gameOver?.observe(viewLifecycleOwner, Observer { gameOverEvent ->
            val gameOver = gameOverEvent?.consume()
            if(gameOver == true) {
                val gameOverFragment = getPositiveOkButtonDialog(R.string.game_over_title, null, DialogInterface.OnDismissListener {
                    endGame()
                })
                gameOverFragment?.apply {
                    setCanceledOnTouchOutside(false)
                    show()
                }
            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessageEvent ->
            val errorMessageDetails = errorMessageEvent?.consume()
            handleErrorMessage(errorMessageDetails)
        })

        connectionStateMonitor.internetConnected.observe(viewLifecycleOwner, Observer<Event<Boolean>> { isInternetConnected ->
            isInternetConnected.consume()?.let{
                onInternetConnectivityChange(it)
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(!viewModel.checkHighScore()) { // if no high score, go back
                    findNavController().popBackStack()
                }
            }
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        loadChimes()
    }

    private fun loadChimes() {
        val gameAudioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).build()
        viewModel.soundPool = SoundPool.Builder().setAudioAttributes(gameAudioAttributes).setMaxStreams(2).build()
        viewModel.successSoundId = viewModel.soundPool?.load(context, R.raw.success_chime, 1)
        viewModel.failureSoundId = viewModel.soundPool?.load(context, R.raw.fail_buzzer, 1)
    }

    override fun onStop() {
        super.onStop()
        viewModel.soundPool?.release()
        viewModel.soundPool = null
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume")
        viewModel.playGame()
    }

    private fun loadDogChallengeImage(dogChallenge: DogChallenge) {
        try {
            Glide.with(this@GameFragment)
                .load(dogChallenge.imageUrl)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_image_not_found)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        resource?.let {
                            binding.dogImage.setImageDrawable(resource)
                            binding.contentLoadingProgressBar.setVisibility(View.INVISIBLE)
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        } catch(ex: Exception) {
            if(!viewModel.checkInternetConnection()) {
                Timber.e(ex, "Failed to load image ${dogChallenge.imageUrl}")
            }
        }
    }

    private fun preloadDogChallengeImages(imagesToPreload: ArrayList<String>) {
        run loop@{
            imagesToPreload.forEach {
                try {
                    Timber.d("Preloading $it")
                    Glide.with(this@GameFragment)
                        .load(it)
                        .error(R.drawable.ic_image_not_found)
                        .preload()

                    imagesToPreload.remove(it)
                } catch (ex: Exception) {
                    if (!viewModel.checkInternetConnection()) {
                        Timber.e(ex, "Failed to preload image ${it}")
                        return@loop // if no internet do not continue to preload images
                    }
                }
            }
        }
    }

    private fun adjustBonesLeftImages(binding: GameFragmentBinding, bonesLeft: Int) {
        binding.apply {
            when(bonesLeft) {
                3 -> {
                    bone1.setImageDrawable(resources.getDrawable(R.drawable.ic_bone_solid))
                    bone2.setImageDrawable(resources.getDrawable(R.drawable.ic_bone_solid))
                    bone3.setImageDrawable(resources.getDrawable(R.drawable.ic_bone_solid))
                }
                2 -> {
                    bone1.setImageDrawable(resources.getDrawable(R.drawable.ic_bone_outline))
                    bone2.setImageDrawable(resources.getDrawable(R.drawable.ic_bone_solid))
                    bone3.setImageDrawable(resources.getDrawable(R.drawable.ic_bone_solid))
                }
                1 -> {
                    bone1.setImageDrawable(resources.getDrawable(R.drawable.ic_bone_outline))
                    bone2.setImageDrawable(resources.getDrawable(R.drawable.ic_bone_outline))
                    bone3.setImageDrawable(resources.getDrawable(R.drawable.ic_bone_solid))
                }
                else -> {
                    bone1.setImageDrawable(resources.getDrawable(R.drawable.ic_bone_outline))
                    bone2.setImageDrawable(resources.getDrawable(R.drawable.ic_bone_outline))
                    bone3.setImageDrawable(resources.getDrawable(R.drawable.ic_bone_outline))
                }
            }
        }
    }

    private fun displayConfetti() {
        binding.konfetti.build()
            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
            .setDirection(0.0, 359.0)
            .setSpeed(1f, 5f)
            .setFadeOutEnabled(true)
            .setTimeToLive(2000L)
            .addShapes(Shape.Square, Shape.Circle)
            .addSizes(Size(12))
            .setPosition(-50f, binding.konfetti.width + 50f, -50f, -50f)
            .streamFor(300, 5000L)
    }

    private fun handleErrorMessage(errorMessageDetails: MessageDetails?) {
        messageDialogFragment?.let{
            if(it.dialog?.isShowing == true) {
                return // An error is already being displayed
            }
        }

        errorMessageDetails?.let{
            val dialogId = if(it.messageCode == QuizMessageCode.ALL_DOGS_LOOKUP_FAILURE) NO_DATA_DIALOG_ID else ERROR_MESSAGE_DIALOG_ID
            it.message.let {
                messageDialogFragment = DialogFragmentCreator.createMessageDialog(DialogContent(dialogId, titleResId = it.titleResourceId, messageResId = it.messageResourceId, messageText = it.messageText, positiveButtonTextResId = R.string.button_dismiss))
                messageDialogFragment?.let{
                    displayDialog(it, dialogId)
                }
            }
        }
        if(errorMessageDetails == null){
            messageDialogFragment?.dismiss()
        }
    }

    private fun displayHighScoreDialog() {
        Timber.d("displayHighScoreDialog")
        val dialogContent = DialogContent(id = HIGHEST_SCORE_DIALOG_ID,
            titleResId = R.string.high_score_achieved_title,
            messageResId = R.string.high_score_achieved_description,
            positiveButtonTextResId = R.string.button_ok,
            layoutResId = R.layout.dialog_edittext)
        val dialogFragment = EditTextDialogFragment.newInstance(dialogContent, viewModel.getLastNameEntered())
        displayDialog(dialogFragment, HIGHEST_SCORE_DIALOG_ID)
    }

    private fun onInternetConnectivityChange(isInternetConnected: Boolean) {
        Timber.d("onInternetConnectivityChange - isInternetConnected: $isInternetConnected, navArgs.game: ${navArgs.game}")
        if(isInternetConnected) {
            navArgs.game?.let{
                viewModel.retryIfInternetFailedPreviously()
            }
        }
    }

    override fun <T> onDialogClosed(dialogId: String, positiveButtonClick: Boolean, newValue: T?) {
        when(dialogId) {
            HIGHEST_SCORE_DIALOG_ID -> {
                newValue?.let{
                    viewModel.recordHighScore(it as String)
                }
                endGame()
            }
            else -> {
                // close and do nothing
            }
        }
    }

    private fun endGame() {
        viewModel.clearGame()
        findNavController().popBackStack()
    }

    companion object {
        private var ERROR_MESSAGE_DIALOG_ID = "au.com.jayne.dogquiz.feature.game.ERROR_MESSAGE_DIALOG_ID"
        private var NO_DATA_DIALOG_ID = "au.com.jayne.dogquiz.feature.game.NO_DATA_DIALOG_ID"
        private var HIGHEST_SCORE_DIALOG_ID = "au.com.jayne.dogquiz.feature.game.HIGHEST_SCORE_DIALOG_ID"
    }
}