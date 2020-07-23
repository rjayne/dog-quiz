package au.com.jayne.dogquiz.common.util

import android.content.Context
import androidx.annotation.StringRes

class ResourceProvider(val context: Context) {

    fun getString(@StringRes id: Int) : String {
        return context.resources.getString(id)
    }

    fun getString(@StringRes id: Int, vararg formatArgs: Any) : String {
        return context.resources.getString(id, *formatArgs)
    }
}