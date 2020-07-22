package au.com.jayne.dogquiz.feature.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import au.com.jayne.dogquiz.R
import au.com.jayne.dogquiz.databinding.GameFragmentBinding
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject

class GameFragment: DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: GameFragmentBinding

    private val viewModel: GameViewModel by lazy { ViewModelProvider(requireActivity(), viewModelFactory).get(
        GameViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.d("onCreate")
        viewModel?.startGame()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = GameFragmentBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@GameFragment)

            onButton1ClickListener = View.OnClickListener {
                viewModel?.update()
            }

            onButton2ClickListener = View.OnClickListener {
                viewModel?.update()
            }

            onButton3ClickListener = View.OnClickListener {
                viewModel?.update()
            }

            onButton4ClickListener = View.OnClickListener {
                viewModel?.update()
            }

            viewModel.score.observe(viewLifecycleOwner, Observer<Int> { userScore ->
                score.text = userScore.toString()
            })

            viewModel.bonesLeft.observe(viewLifecycleOwner, Observer<Int> { bonesLeft ->
                adjustBonesLeftImages(binding, bonesLeft)
            })
        }



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
}