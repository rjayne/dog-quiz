package au.com.jayne.dogquiz.common.ui

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.Keep
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import au.com.jayne.dogquiz.R
import au.com.jayne.dogquiz.domain.model.DialogContent

open class BasicDialogFragment: DialogFragment(), DialogInterface.OnClickListener {

    private var contentId: CharSequence = "au.com.jayne.dogquiz.common.ui.DIALOG"
    private var dialogTitle: CharSequence? = null
    private var positiveButtonText: CharSequence? = null
    private var negativeButtonText: CharSequence? = null
    private var dialogMessage: CharSequence? = null
    @LayoutRes
    private var dialogLayoutRes: Int = 0

    var dialogIconRes: Int? = null

    /** Which button was clicked.  */
    private var whichButtonClicked: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val content = arguments?.getParcelable<DialogContent>(ARG_CONTENT)
        if (savedInstanceState == null) {
            content?.apply {
                contentId = id
                dialogTitle = resources.getString(titleResId)
                positiveButtonTextResId?.let{
                    positiveButtonText = resources.getString(it)
                }
                negativeButtonTextResId?.let{
                    negativeButtonText = resources.getString(it)
                }
                messageResId?.let{
                    dialogMessage = resources.getString(it)
                }
                dialogLayoutRes = layoutResId
                dialogIconRes = iconResourceId
            }

        } else {
            savedInstanceState.getCharSequence(SAVE_STATE_ID)?.let{
                contentId = it
            }
            dialogTitle = savedInstanceState.getCharSequence(SAVE_STATE_TITLE)
            positiveButtonText = savedInstanceState.getCharSequence(SAVE_STATE_POSITIVE_TEXT)
            negativeButtonText = savedInstanceState.getCharSequence(SAVE_STATE_NEGATIVE_TEXT)
            dialogMessage = savedInstanceState.getCharSequence(SAVE_STATE_MESSAGE)
            dialogLayoutRes = savedInstanceState.getInt(SAVE_STATE_LAYOUT, 0)
            dialogIconRes = savedInstanceState.getInt(SAVE_STATE_ICON)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putCharSequence(SAVE_STATE_ID, contentId)
        outState.putCharSequence(SAVE_STATE_TITLE, dialogTitle)
        outState.putCharSequence(SAVE_STATE_POSITIVE_TEXT, positiveButtonText)
        outState.putCharSequence(SAVE_STATE_NEGATIVE_TEXT, negativeButtonText)
        outState.putCharSequence(SAVE_STATE_MESSAGE, dialogMessage)
        outState.putInt(SAVE_STATE_LAYOUT, dialogLayoutRes)
        dialogIconRes?.let{
            outState.putInt(SAVE_STATE_ICON, it)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Create the dialog
        return createDialogBuilder().create()
    }

    fun createDialogBuilder(): AlertDialog.Builder {
        val context = requireActivity()
        whichButtonClicked = DialogInterface.BUTTON_NEGATIVE

        val builder = AlertDialog.Builder(context)
            .setTitle(dialogTitle)

        positiveButtonText?.let{
            builder.setPositiveButton(positiveButtonText, this)
        }

        dialogIconRes?.let{
            builder.setIcon(resources.getDrawable(it, null))
        }

        negativeButtonText?.let{
            builder.setNegativeButton(negativeButtonText, this)
        }

        val contentView = onCreateDialogView(context)
        if (contentView != null) {
            builder.setView(contentView)
        } else {
            builder.setMessage(dialogMessage)
        }

        return builder
    }

    /**
     * Creates the content view for the dialog (if a custom content view is required).
     * By default, it inflates the dialog layout resource if it is set.
     *
     * @return The content view for the dialog
     * @see DialogPreference.setLayoutResource
     */
    protected fun onCreateDialogView(context: Context): View? {
        val resId = dialogLayoutRes
        if (resId == 0) {
            return null
        }

        val inflater = LayoutInflater.from(context)
        return inflater.inflate(resId, null)
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        whichButtonClicked = which
    }

    // If called by an activity, no response returned onDismiss. Implement when required.
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        getDialogTargetFragment()?.onDialogClosed(contentId.toString(),whichButtonClicked == DialogInterface.BUTTON_POSITIVE, null)
    }

    protected fun getDialogTargetFragment() : DialogTargetFragment? {
        return targetFragment as DialogTargetFragment?
    }

    companion object {
        const val ARG_CONTENT = "content"

        private const val SAVE_STATE_ID = "BasicDialogFragment.contentId"
        private const val SAVE_STATE_TITLE = "BasicDialogFragment.title"
        private const val SAVE_STATE_POSITIVE_TEXT = "BasicDialogFragment.positiveText"
        private const val SAVE_STATE_NEGATIVE_TEXT = "BasicDialogFragment.negativeText"
        private const val SAVE_STATE_MESSAGE = "BasicDialogFragment.message"
        private const val SAVE_STATE_LAYOUT = "BasicDialogFragment.layout"
        private const val SAVE_STATE_ICON = "BasicDialogFragment.icon"

        fun newInstance(content: DialogContent): BasicDialogFragment {
            val fragment = BasicDialogFragment()
            val b = Bundle(1)
            b.putParcelable(ARG_CONTENT, content)
            fragment.arguments = b
            return fragment
        }
    }
}