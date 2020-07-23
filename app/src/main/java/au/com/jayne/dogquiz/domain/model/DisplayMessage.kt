package au.com.jayne.dogquiz.domain.model

import androidx.annotation.Keep

@Keep
data class DisplayMessage(val id: String, val titleResId: Int, val descriptionResId: Int, val positiveButtonTextResId: Int?)