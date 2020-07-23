package au.com.jayne.dogquiz.common.ui

import androidx.annotation.Keep

@Keep
interface DialogTargetFragment {
    fun <T> onDialogClosed(dialogId: String, positiveButtonClick: Boolean, newValue: T? = null)
}