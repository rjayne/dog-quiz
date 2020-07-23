package au.com.jayne.dogquiz.domain.exception

import androidx.annotation.Keep
import au.com.jayne.dogquiz.domain.model.MessageDetails

@Keep
open class QuizException(val messageDetails: MessageDetails) : RuntimeException()