package au.com.jayne.dogquiz.common.ui

import androidx.annotation.Keep
import androidx.fragment.app.DialogFragment
import au.com.jayne.dogquiz.domain.model.DialogContent
import au.com.jayne.dogquiz.domain.model.DisplayMessage
import timber.log.Timber

@Keep
object DialogFragmentCreator {

    fun createMessageDialog(displayMessage: DisplayMessage): DialogFragment {
        Timber.d("createMessageDialog - ${displayMessage.id}")
        val dialogContent = DialogContent(id = displayMessage.id,
            titleResId = displayMessage.titleResId,
            messageResId = displayMessage.descriptionResId,
            positiveButtonTextResId = displayMessage.positiveButtonTextResId)
        return BasicDialogFragment.newInstance(dialogContent)
    }

    fun createMessageDialog(dialogContent: DialogContent): DialogFragment {
        Timber.d("createMessageDialog - ${dialogContent.id}")
        return BasicDialogFragment.newInstance(dialogContent)
    }

}