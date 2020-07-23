package au.com.jayne.dogquiz.common.ui

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.Keep
import au.com.jayne.dogquiz.R
import au.com.jayne.dogquiz.domain.model.DialogContent

@Keep
class NonTitleImageDialogFragment: BasicDialogFragment() {

    private var imageView: ImageView? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = createDialogBuilder()
        builder.setIcon(null)
        // Create the dialog
        return builder.create()
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        dialogIconRes?.let{ dialogIconRes ->

            imageView = view.findViewById(R.id.image)

            checkNotNull(imageView) { "Dialog view must contain an Image with id @+id/image" }

            imageView?.apply {
                requestFocus()
                setImageResource(dialogIconRes)
            }
        }
    }

    companion object {
        fun newInstance(content: DialogContent): NonTitleImageDialogFragment {
            val fragment = NonTitleImageDialogFragment()
            val b = Bundle(1)
            b.putParcelable(ARG_CONTENT, content)
            fragment.arguments = b
            return fragment
        }
    }
}