package au.com.jayne.dogquiz.common.ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import au.com.jayne.dogquiz.R
import au.com.jayne.dogquiz.domain.model.DialogContent

open class EditTextDialogFragment: StandardInputDialogFragment<String>() {

    private var editText: EditText? = null

    private var textValue: CharSequence? = null

    // The OnBindEditTextListener used to configure the EditText
    // displayed in the corresponding dialog view for this dialog.
    var onBindEditTextListener: OnBindEditTextListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            textValue = savedInstanceState.getCharSequence(SAVE_STATE_TEXT)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(SAVE_STATE_TEXT, getSubmittedValue())
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        editText = view.findViewById(R.id.editText_value)

        checkNotNull(editText) { "Dialog view must contain an EditText with id" + " @+id/editText_value" }

        editText?.apply {
            requestFocus()
            setText(textValue)
            // Place cursor at the end
            setSelection(text.length)
            onBindEditTextListener?.onBindEditText(this)
        }
    }

    override fun needInputMethod(): Boolean {
        // We want the input method to show, if possible, when dialog is displayed
        return true
    }

    override fun getSubmittedValue(): String? {
        return editText?.text.toString()
    }

    companion object {
        private const val SAVE_STATE_TEXT = "EditTextDialogFragment.text"

        fun newInstance(content: DialogContent): EditTextDialogFragment {
            val fragment = EditTextDialogFragment()
            val b = Bundle(1)
            b.putParcelable(ARG_CONTENT, content)
            fragment.arguments = b
            return fragment
        }
    }

    /**
     * Interface definition for a callback to be invoked when the corresponding dialog view for
     * this DialogContent is bound. This allows you to customize the [EditText] displayed
     * in the dialog, such as setting a max length or a specific input type.
     */
    interface OnBindEditTextListener {
        /**
         * Called when the dialog view for this DialogContent has been bound, allowing you to
         * customize the [EditText] displayed in the dialog.
         *
         * @param editText The [EditText] displayed in the dialog
         */
        fun onBindEditText(editText: EditText)
    }
}