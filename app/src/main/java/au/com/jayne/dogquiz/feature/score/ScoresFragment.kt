package au.com.jayne.dogquiz.feature.score

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import au.com.jayne.dogquiz.databinding.ScoresFragmentBinding
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject

class ScoresFragment: DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: ScoresFragmentBinding

    private val viewModel: ScoresViewModel by lazy { ViewModelProvider(requireActivity(), viewModelFactory).get(
        ScoresViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")

        viewModel.refreshHighScores()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ScoresFragmentBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@ScoresFragment)
            viewModel = this@ScoresFragment.viewModel
        }
        return binding.root
    }

}