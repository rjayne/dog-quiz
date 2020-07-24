package au.com.jayne.dogquiz.domain.model

import androidx.annotation.Keep

@Keep
data class MessageContainer(val messageResourceId: Int?, val messageText: String? = null, val titleResourceId: Int) {
    constructor(messageText: String, titleResourceId: Int) : this(null, messageText, titleResourceId)
    constructor(messageResourceId: Int, titleResourceId: Int) : this(messageResourceId, null, titleResourceId)
}
