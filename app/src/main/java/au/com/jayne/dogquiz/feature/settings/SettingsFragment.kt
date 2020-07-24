package au.com.jayne.dogquiz.feature.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import au.com.jayne.dogquiz.R
import au.com.jayne.dogquiz.common.dagger.preference.DaggerPreferenceFragmentCompat
import au.com.jayne.dogquiz.common.extensions.getPositiveOkButtonDialog
import au.com.jayne.dogquiz.common.preference.YesNoPreference
import au.com.jayne.dogquiz.common.preference.YesNoPreferenceDialogFragment
import au.com.jayne.dogquiz.common.util.SharedPreferenceStorage
import au.com.jayne.dogquiz.domain.model.SharedPreferenceKey
import timber.log.Timber
import javax.inject.Inject

/**
 * This {@link Fragment} displays a hierarchy of {@link Preference} objects to the user. It's parent
 * PreferenceFragmentCompat handles persisting values to the device. Preference values are
 * located in settings_preferences.xml.
 */
class SettingsFragment: DaggerPreferenceFragmentCompat() {

    @Inject
    lateinit var sharedPreferenceStorage: SharedPreferenceStorage

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState) //as LinearLayout?
        view?.apply {
            setBackground(resources.getDrawable(R.drawable.bg_poodle, null))
            setPaddingRelative(0, resources.getDimensionPixelSize(R.dimen.settings_top_margin).toInt(), 0, 0)
        }

        return view
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)

        val resetScorePreference: YesNoPreference? = findPreference(SharedPreferenceKey.GAME_SCORES.keyName)
        resetScorePreference?.apply {
            onCompletionListener = object: YesNoPreference.OnCompletionListener{
                override fun onYes() {
                    sharedPreferenceStorage.resetHighScores()
                    getPositiveOkButtonDialog(R.string.settings_reset_scores_complete_title, R.string.settings_reset_scores_complete_message)?.show()
                }

                override fun onNo() {
                    Timber.d("User decided to not reset the high scores.")
                }
            }
        }
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        Timber.d("onDisplayPreferenceDialog - ${preference.key}")
        // check if dialog is already showing
        if (parentFragmentManager.findFragmentByTag(DIALOG_FRAGMENT_TAG) != null)
        {
            return
        }

        // Check if the preference is a custom Preferences
        var dialogFragment: DialogFragment? = null
        if (preference is YesNoPreference) {
            dialogFragment = YesNoPreferenceDialogFragment.newInstance(preference.getKey())
        }

        // If it was one of our custom Preferences, show its dialog
        if (dialogFragment != null) {
            dialogFragment.apply {
                setTargetFragment(this@SettingsFragment, 0)
                show(this@SettingsFragment.parentFragmentManager, DIALOG_FRAGMENT_TAG)
            }
            Timber.d("onDisplayPreferenceDialog - about to Execute Pending Txns")
            parentFragmentManager.executePendingTransactions()
        } else {
            // Could not be handled here. Try with the super method.
            Timber.d("onDisplayPreferenceDialog - call super")
            super.onDisplayPreferenceDialog(preference)
        }
    }

    companion object {
        var DIALOG_FRAGMENT_TAG = "androidx.preference.PreferenceFragment.DIALOG"
    }
}