package au.com.jayne.dogquiz.common.ui

import androidx.annotation.Keep
import androidx.fragment.app.DialogFragment
import au.com.jayne.dogquiz.R
import au.com.jayne.dogquiz.domain.model.DialogContent
import au.com.jayne.dogquiz.domain.model.DisplayMessage
import au.com.jayne.dogquiz.domain.model.MessageContainer
import timber.log.Timber

@Keep
object DialogFragmentCreator {

    var ERROR_MESSAGE_DIALOG_ID = "au.com.jayne.dogquiz.common.ui.ERROR_MESSAGE_DIALOG_ID"

    fun createGameErrorMessageDialog(): DialogFragment {
        return createErrorMessageDialog(R.string.error_game_data_title, R.string.error_game_data_message)
    }

    fun createErrorMessageDialog(titleResId: Int, descriptionResId: Int): DialogFragment {
        Timber.d("createErrorMessageDialog")
        val dialogContent = DialogContent(id = ERROR_MESSAGE_DIALOG_ID,
            layoutResId = R.layout.dialog_nontitleimage,
            titleResId = titleResId,
            messageResId = descriptionResId,
            iconResourceId = R.drawable.ic_alert,
            positiveButtonTextResId = R.string.button_dismiss)
        return NonTitleImageDialogFragment.newInstance(dialogContent)
    }

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