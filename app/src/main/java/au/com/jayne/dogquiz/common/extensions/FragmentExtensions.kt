package au.com.jayne.dogquiz.common.extensions

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import timber.log.Timber

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

fun Fragment.displayDialog(dialogFragment: DialogFragment, tag: String) {
    val parentFragment = this
    parentFragmentManager?.let{ fragmentManager ->
        // check if dialog is already showing
        if (fragmentManager.findFragmentByTag(tag) != null)
        {
            Timber.d("displayDialog - Already displayed. Do not display again $tag")
            return
        }
        Timber.d("displayDialog - $tag")
        dialogFragment.apply {
            setTargetFragment(parentFragment, 0)
            show(fragmentManager, tag)
        }
    }
}