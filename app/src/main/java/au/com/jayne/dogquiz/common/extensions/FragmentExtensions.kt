package au.com.jayne.dogquiz.common.extensions

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

fun Fragment.getPositiveOkButtonDialog(title: Int, message: Int?, onDismissListener: DialogInterface.OnDismissListener? = null): AlertDialog? {
    context?.let {
        val builder = AlertDialog.Builder(it)
        builder.setTitle(getString(title))
        message?.let{
            builder.setMessage(getString(it))
        }
        builder.setPositiveButton(android.R.string.ok, null)
        builder.setOnDismissListener(onDismissListener)
        return builder.create()
    }

    return null
}