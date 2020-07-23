package au.com.jayne.dogquiz.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Parcelize @Keep
data class DialogContent(val id: String,
                         val titleResId: Int,
                         val messageResId: Int? = null,
                         val messageText: String? = null,
                         val iconResourceId: Int? = null,
                         val positiveButtonTextResId: Int? = null,
                         val negativeButtonTextResId: Int? = null,
                         val layoutResId: Int = 0): Parcelable
