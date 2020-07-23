package au.com.jayne.dogquiz.feature.game

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import au.com.jayne.dogquiz.R
import au.com.jayne.dogquiz.common.extensions.displayDialog
import au.com.jayne.dogquiz.common.network.ConnectionStateMonitor
import au.com.jayne.dogquiz.common.ui.DialogFragmentCreator
import au.com.jayne.dogquiz.databinding.GameFragmentBinding
import au.com.jayne.dogquiz.domain.model.Event
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject


class GameFragment: DaggerFragment() {

    @Inject
    lateinit var connectionStateMonitor: ConnectionStateMonitor

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: GameFragmentBinding

    private val viewModel: GameViewModel by lazy { ViewModelProvider(requireActivity(), viewModelFactory).get(
        GameViewModel::class.java) }

    val navArgs: GameFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")

        connectionStateMonitor.internetConnected.observe(this, Observer<Event<Boolean>> { isInternetConnected ->
            isInternetConnected.consume()?.let{
                onInternetConnectivityChange(it)
            }
        })

        viewModel.startGame(navArgs.game)
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


                Glide.with(this@GameFragment)
                    .load(dogChallenge.imageUrl)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            resource?.let{
                                dogImage.setImageDrawable(resource)
                                binding.contentLoadingProgressBar.setVisibility(View.INVISIBLE)
                            }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
            })

            viewModel?.imagesToPreload?.observe(viewLifecycleOwner, Observer { imagesToPreload ->
                imagesToPreload.forEach {
                    Timber.d("Preloading $it")
                    Glide.with(this@GameFragment)
                        .load(it)
                        .preload()

                    imagesToPreload.remove(it)
                }
            })
        }

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessageEvent ->
            val errorMessage = errorMessageEvent.consume()
            errorMessage?.let{
                Timber.i(it.toString())
                val dialogFragment = DialogFragmentCreator.createErrorMessageDialog(it.titleResourceId!!, it.messageResourceId!!) // TODO fix this
                displayDialog(dialogFragment, DialogFragmentCreator.ERROR_MESSAGE_DIALOG_ID)
            }
        })

        return binding.root
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

    private fun onInternetConnectivityChange(isInternetConnected: Boolean) {
        Timber.d("onInternetConnectivityChange - isInternetConnected: $isInternetConnected, navArgs.game: ${navArgs.game}")
        if(isInternetConnected) {
            navArgs.game?.let{
                viewModel.retryIfInternetFailedPreviously()
            }
        }
    }
}