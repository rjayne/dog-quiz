package au.com.jayne.dogquiz.domain.repo

import au.com.jayne.dogquiz.R
import au.com.jayne.dogquiz.common.util.ResourceProvider
import au.com.jayne.dogquiz.domain.exception.QuizErrorException
import au.com.jayne.dogquiz.domain.model.ErrorResponse
import au.com.jayne.dogquiz.domain.model.MessageDetails
import au.com.jayne.dogquiz.domain.service.SimpleMoshiJsonParser
import timber.log.Timber

class DogRepoErrorDetailsHandler(val resourceProvider: ResourceProvider): RepoErrorDetailsHandler {

    override fun handleError(errorBodyString: String, messageCode: Int, errorLogMessage: String) {
        val errorBody = getErrorBody(errorBodyString)
        handleError(errorBody, messageCode, errorLogMessage)
    }

    private fun getErrorBody(errorBodyJson: String?): ErrorResponse? {
        try {
            errorBodyJson?.let {
                return SimpleMoshiJsonParser.fromJson(it, ErrorResponse::class.java)
            }
        } catch(ex: Exception) {
            Timber.w(ex, "Could not parse ErrorBody json from $errorBodyJson")
        }
        return null
    }

    private fun handleError(errorBody: ErrorResponse?, messageCode: Int, errorLogMessage: String) {
        if(errorBody?.status.equals(ERROR)) {
            errorBody?.message?.let{
                    Timber.e("handleError $errorLogMessage - ${errorBody.message}")
                    throw QuizErrorException(getErrorDetails(messageCode, errorBody.message))
            }

            Timber.e("handleError - No error reason for messageCode $messageCode. Sending generic error message. Please investigate.")
            throw QuizErrorException(getUnexpectedErrorDetails(messageCode))
        }
    }

    fun getErrorDetails(messageCode: Int, errorReason: String): MessageDetails {
        return MessageDetails(messageCode = messageCode, message = errorReason, titleResourceId = R.string.error_unexpected_title)
    }

    override fun getUnexpectedErrorDetails(messageCode: Int): MessageDetails {
        return MessageDetails(messageCode = messageCode, messageResourceId = R.string.error_unexpected_message, titleResourceId = R.string.error_unexpected_title)
    }

    override fun getNetworkExceptionDetails(messageCode: Int, code: Int): MessageDetails {
        val message = resourceProvider.getString(R.string.error_lookup_network_message_with_code, code)
        return MessageDetails(messageCode = messageCode, message = message, titleResourceId = R.string.error_lookup_network_title)
    }

    override fun getNetworkExceptionDetails(messageCode: Int): MessageDetails {
        return MessageDetails(messageCode = messageCode, messageResourceId = R.string.error_lookup_network_message, titleResourceId = R.string.error_lookup_network_title)
    }

    companion object {
        private const val ERROR = "error"
    }
}