package au.com.jayne.dogquiz.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import au.com.jayne.dogquiz.R
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
enum class Game(val nameResId: Int, val breed: String? = null): Parcelable {
    HOUND_QUIZ(R.string.game_hound_name, "hound"),
    SPANIEL_QUIZ(R.string.game_spaniel_name, "spaniel"),
    TERRIER_QUIZ(R.string.game_terrier_name, "terrier"),
    DOGGY_QUIZ(R.string.game_doggy_name)
}