package au.com.jayne.dogquiz.domain.model

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class HighScore(
    @Json(name = "name") val name: String,
    @Json(name = "score") val score: Int
)

@Keep
@JsonClass(generateAdapter = true)
data class GameScores(
    @Json(name = "highScoresMap") val highScoresMap: HashMap<Game, HighScore>
)