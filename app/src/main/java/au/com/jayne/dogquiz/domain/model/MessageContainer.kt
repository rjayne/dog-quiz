package au.com.jayne.dogquiz.domain.model

import androidx.annotation.Keep

@Keep
data class MessageContainer(val messageResourceId: Int?, val messageText: String? = null, val titleResourceId: Int?, val titleText: String? = null) {
    constructor(messageText: String, titleResourceId: Int) : this(null, messageText, titleResourceId, null)
    constructor(messageResourceId: Int, titleResourceId: Int) : this(messageResourceId, null, titleResourceId, null)
    constructor(messageText: String, titleText: String) : this(null, messageText, null, titleText)
    constructor(messageResourceId: Int, titleText: String) : this(messageResourceId, null, null, titleText)
}
