package au.com.jayne.dogquiz.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 *  {
        "message": "https://images.dog.ceo/breeds/hound-afghan/n02088094_7683.jpg",
        "status": "success"
    }
 */
@JsonClass(generateAdapter = true)
data class ImageResponse(
    @Json(name = "message") val url: String,
    @Json(name = "status") val status: String
)

@JsonClass(generateAdapter = true)
data class BreedResponse(
    @Json(name = "message") val message: Map<String, Array<String>>,
    @Json(name = "status") val status: String
)

/**
 * {
    "message": [
        "afghan",
        "basset",
        "blood",
        "english",
        "ibizan",
        "plott",
        "walker"
    ],
    "status": "success"
    }
 */
@JsonClass(generateAdapter = true)
data class SubBreedResponse(
    @Json(name = "message") val message: Array<String>,
    @Json(name = "status") val status: String
)

/**
{
    "status": "error",
    "message": "Breed not found (master breed does not exist)",
    "code": 404
}
*/
@JsonClass(generateAdapter = true)
data class ErrorResponse(
    @Json(name = "status") val status: String,
    @Json(name = "message") val message: String?,
    @Json(name = "code") val code: String?
)