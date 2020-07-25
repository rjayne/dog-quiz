package au.com.jayne.dogquiz.common.ui

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.preference.DialogPreference
import au.com.jayne.dogquiz.R
import au.com.jayne.dogquiz.domain.model.DialogContent

/**
 * Abstract base class which presents a dialog associated with a {@link DialogContent}. Since
 * the content object may not be available during fragment re-creation, the necessary
 * information for displaying the dialog is read once during the initial call to
 * {@link #onCreate(Bundle)} and saved/restored in the saved instance state. Custom subclasses
 * should also follow this pattern.
 */
abstract class StandardInputDialogFragment<T>: DialogFragment(), DialogInterface.OnClickListener {

    private lateinit var contentId: CharSequence
    private var dialogTitle: CharSequence? = null
    private var positiveButtonText: CharSequence? = null
    private var negativeButtonText: CharSequence? = null
    private var dialogMessage: CharSequence? = null
    @LayoutRes
    private var dialogLayoutRes: Int = 0

    private var dialogIcon: BitmapDrawable? = null

    /** Which button was clicked.  */
    private var whichButtonClicked: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val content = requireArguments().getParcelable<DialogContent>(ARG_CONTENT)
        if (savedInstanceState == null) {
            content?.apply {
                contentId = id
                dialogTitle = resources.getString(titleResId)
                positiveButtonText = resources.getString(positiveButtonTextResId)
                negativeButtonTextResId?.let{
                    negativeButtonText = resources.getString(it)
                }
                messageResId?.let{
                    dialogMessage = resources.getString(it)
                }
                dialogLayoutRes = layoutResId
            }

        } else {
            contentId = savedInstanceState.getCharSequence(SAVE_STATE_ID)!!
            dialogTitle = savedInstanceState.getCharSequence(SAVE_STATE_TITLE)
            positiveButtonText = savedInstanceState.getCharSequence(SAVE_STATE_POSITIVE_TEXT)
            negativeButtonText = savedInstanceState.getCharSequence(SAVE_STATE_NEGATIVE_TEXT)
            dialogMessage = savedInstanceState.getCharSequence(SAVE_STATE_MESSAGE)
            dialogLayoutRes = savedInstanceState.getInt(SAVE_STATE_LAYOUT, 0)
            val bitmap = savedInstanceState.getParcelable<Bitmap>(SAVE_STATE_ICON)
            bitmap?.let {
                dialogIcon = BitmapDrawable(resources, it)
            }
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
        dialogIcon?.let{
            outState.putParcelable(SAVE_STATE_ICON, it.bitmap)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireActivity()
        whichButtonClicked = DialogInterface.BUTTON_NEGATIVE

        val builder = AlertDialog.Builder(context)
            .setTitle(dialogTitle)
            .setIcon(dialogIcon)
            .setPositiveButton(positiveButtonText, this)
            .setNegativeButton(negativeButtonText, this)

        val contentView = onCreateDialogView(context)
        if (contentView != null) {
            onBindDialogView(contentView)
            builder.setView(contentView)
        } else {
            builder.setMessage(dialogMessage)
        }

        onPrepareDialogBuilder(builder)

        // Create the dialog
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        if (needInputMethod()) {
            requestInputMethod(dialog)
        }

        return dialog
    }

    /**
     * Prepares the dialog builder to be shown when the preference is clicked.
     * Use this to set custom properties on the dialog.
     *
     *
     * Do not [AlertDialog.Builder.create] or [AlertDialog.Builder.show].
     */
    protected fun onPrepareDialogBuilder(builder: AlertDialog.Builder) {}

    /**
     * Returns whether there is a need to display a soft input method when the dialog is
     * displayed. Default is false. Subclasses should override this method if they need the soft
     * input method brought up automatically.
     *
     *
     * Note: If your application targets P or above, ensure your subclass manually requests
     * focus (ideally in [.onBindDialogView]) for the input field in order to
     * correctly attach the input method to the field.
     *
     * @hide
     */
    open fun needInputMethod(): Boolean {
        return false
    }

    /**
     * Sets the required flags on the dialog window to enable input method window to show up.
     */
    private fun requestInputMethod(dialog: Dialog) {
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
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

    /**
     * Binds views in the content view of the dialog to data.
     * Make sure to call through to the superclass implementation.
     *
     * @param view The content view of the dialog, if it is custom
     */
    open fun onBindDialogView(view: View) {
        val dialogMessageView = view.findViewById<View>(R.id.text_message)

        if (dialogMessageView != null) {
            val message = dialogMessage
            var newVisibility = View.GONE

            if (!TextUtils.isEmpty(message)) {
                if (dialogMessageView is TextView) {
                    dialogMessageView.text = message
                }

                newVisibility = View.VISIBLE
            }

            if (dialogMessageView.visibility != newVisibility) {
                dialogMessageView.visibility = newVisibility
            }
        }
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        whichButtonClicked = which
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        getDialogTargetFragment().onDialogClosed(contentId.toString(),whichButtonClicked == DialogInterface.BUTTON_POSITIVE, getSubmittedValue())
    }

    abstract fun getSubmittedValue(): T?

    protected fun getDialogTargetFragment() : DialogTargetFragment {
        return targetFragment as DialogTargetFragment
    }

    companion object {
        const val ARG_CONTENT = "content"

        private const val SAVE_STATE_ID = "StandardInputDialogFragment.contentId"
        private const val SAVE_STATE_TITLE = "StandardInputDialogFragment.title"
        private const val SAVE_STATE_POSITIVE_TEXT = "StandardInputDialogFragment.positiveText"
        private const val SAVE_STATE_NEGATIVE_TEXT = "StandardInputDialogFragment.negativeText"
        private const val SAVE_STATE_MESSAGE = "StandardInputDialogFragment.message"
        private const val SAVE_STATE_LAYOUT = "StandardInputDialogFragment.layout"
        private const val SAVE_STATE_ICON = "StandardInputDialogFragment.icon"
    }
}