package au.com.jayne.dogquiz.feature.selection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import au.com.jayne.dogquiz.databinding.GameSelectionFragmentBinding
import au.com.jayne.dogquiz.domain.model.Game
import au.com.jayne.dogquiz.feature.main.MainViewModel
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject

class GameSelectionFragment: DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: GameSelectionFragmentBinding

    private val viewModel: MainViewModel by lazy { ViewModelProvider(requireActivity(), viewModelFactory).get(MainViewModel::class.java) }

    private val onGameSelectedListener = object: OnGameSelectedListener {
        override fun play(game: Game) {
            Timber.d("play $game")
            viewModel.gameSelected = game
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = GameSelectionFragmentBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@GameSelectionFragment)
            onGameSelectedListener = this@GameSelectionFragment.onGameSelectedListener
        }

        return binding.root
    }

    interface OnGameSelectedListener {
        fun play(game: Game)
    }
}