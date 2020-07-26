package au.com.jayne.dogquiz.common.util

import androidx.annotation.StringRes

interface ResourceProvider {

    fun getString(@StringRes id: Int) : String

    fun getString(@StringRes id: Int, vararg formatArgs: Any) : String

}