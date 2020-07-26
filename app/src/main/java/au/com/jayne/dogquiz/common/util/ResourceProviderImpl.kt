package au.com.jayne.dogquiz.common.util

import android.content.Context
import androidx.annotation.StringRes

class ResourceProviderImpl(val context: Context): ResourceProvider {

    override fun getString(@StringRes id: Int) : String {
        return context.resources.getString(id)
    }

    override fun getString(@StringRes id: Int, vararg formatArgs: Any) : String {
        return context.resources.getString(id, *formatArgs)
    }
}