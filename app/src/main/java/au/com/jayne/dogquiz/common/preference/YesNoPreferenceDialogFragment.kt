package au.com.jayne.dogquiz.common.preference

import android.os.Bundle
import au.com.jayne.dogquiz.common.preference.dagger.DaggerPreferenceDialogFragmentCompat

class YesNoPreferenceDialogFragment: DaggerPreferenceDialogFragmentCompat() {
    override fun onDialogClosed(positiveResult: Boolean) {
        val preference = getYesNoPreference()
        if (positiveResult) {
            preference.onCompletionListener?.onYes()
        } else {
            preference.onCompletionListener?.onNo()
        }
    }

    private fun getYesNoPreference(): YesNoPreference {
        return preference as YesNoPreference
    }

    companion object {
        fun newInstance(key: String): YesNoPreferenceDialogFragment {
            val fragment = YesNoPreferenceDialogFragment()
            val b = Bundle(1)
            b.putString(ARG_KEY, key)
            fragment.arguments = b
            return fragment
        }
    }
}