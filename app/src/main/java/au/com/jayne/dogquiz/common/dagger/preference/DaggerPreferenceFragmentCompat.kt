package au.com.jayne.dogquiz.common.dagger.preference

import android.content.Context
import androidx.preference.PreferenceFragmentCompat
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * A {@link PreferenceFragmentCompat} that injects its members in {@link #onAttach(Context)} and can be used to
 * inject child {@link PreferenceFragmentCompat}s attached to it. Note that when this fragment gets reattached, its
 * members will be injected again.
 */
open abstract class DaggerPreferenceFragmentCompat : PreferenceFragmentCompat(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun androidInjector(): AndroidInjector<Any>? {
        return androidInjector
    }

}