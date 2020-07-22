package au.com.jayne.dogquiz.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import au.com.jayne.dogquiz.R
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
enum class Game(val nameResId: Int): Parcelable {
    HOUND_QUIZ(R.string.game_hound_name),
    SPANIEL_QUIZ(R.string.game_spaniel_name),
    TERRIER_QUIZ(R.string.game_terrier_name),
    DOGGY_QUIZ(R.string.game_doggy_name)
}