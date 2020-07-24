package au.com.jayne.dogquiz.feature.game

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import au.com.jayne.dogquiz.R
import au.com.jayne.dogquiz.common.extensions.displayDialog
import au.com.jayne.dogquiz.common.network.ConnectionStateMonitor
import au.com.jayne.dogquiz.common.ui.DialogFragmentCreator
import au.com.jayne.dogquiz.common.ui.DialogTargetFragment
import au.com.jayne.dogquiz.databinding.GameFragmentBinding
import au.com.jayne.dogquiz.domain.model.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import dagger.android.support.DaggerFragment
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

    override fun onResume() {
        super.onResume()
        Timber.d("onResume")
        viewModel.playGame()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = GameFragmentBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@GameFragment)
            if(viewModel?.dogChallenge?.value == null) {
                contentLoadingProgressBar.setVisibility(View.VISIBLE)
            }

            viewModel = this@GameFragment.viewModel

            viewModel?.score?.observe(viewLifecycleOwner, Observer<Int> { userScore ->
                score.text = userScore.toString()
            })

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
        }

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessageEvent ->
            val errorMessageDetails = errorMessageEvent?.consume()
            handleErrorMessage(errorMessageDetails)
        })

        connectionStateMonitor.internetConnected.observe(viewLifecycleOwner, Observer<Event<Boolean>> { isInternetConnected ->
            isInternetConnected.consume()?.let{
                onInternetConnectivityChange(it)
            }
        })

        return binding.root
    }

    private fun loadDogChallengeImage(dogChallenge: DogChallenge) {
        try {
            Glide.with(this@GameFragment)
                .load(dogChallenge.imageUrl)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_alert)
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
                        .error(R.drawable.ic_alert)
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
            NO_DATA_DIALOG_ID -> {
//                findNavController().popBackStack()
            }
            else -> {
                // close and do nothing
            }
        }
    }

    companion object {
        var ERROR_MESSAGE_DIALOG_ID = "au.com.jayne.dogquiz.common.ui.ERROR_MESSAGE_DIALOG_ID"
        var NO_DATA_DIALOG_ID = "au.com.jayne.dogquiz.common.ui.NO_DATA_DIALOG_ID"
    }
}