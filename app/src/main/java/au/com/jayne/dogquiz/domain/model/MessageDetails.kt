package au.com.jayne.dogquiz.domain.model

import androidx.annotation.Keep
import au.com.jayne.dogquiz.R

@Keep
data class MessageDetails(val messageCode: Int, val message: MessageContainer) {
    constructor(messageCode: Int): this(messageCode, MessageContainer(messageText = "", titleResourceId = R.string.error_unexpected_title))
    constructor(messageCode: Int, messageResourceId: Int): this(messageCode, MessageContainer(messageResourceId, titleResourceId = R.string.error_unexpected_title))
    constructor(messageCode: Int, message: String): this(messageCode, MessageContainer(message, titleResourceId = R.string.error_unexpected_title))
    constructor(messageCode: Int, message: String, titleResourceId: Int): this(messageCode, MessageContainer(message, titleResourceId))
    constructor(messageCode: Int, messageResourceId: Int, titleResourceId: Int): this(messageCode, MessageContainer(messageResourceId, titleResourceId = titleResourceId))
}
