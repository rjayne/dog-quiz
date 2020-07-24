package au.com.jayne.dogquiz.domain.repo

import au.com.jayne.dogquiz.domain.model.MessageDetails
import au.com.jayne.dogquiz.domain.model.QuizMessageCode

interface RepoErrorDetailsHandler {
    fun handleError(errorBody: String, messageCode: QuizMessageCode, errorLogMessage: String)

    fun getUnexpectedErrorDetails(messageCode: QuizMessageCode): MessageDetails

    fun getNetworkExceptionDetails(messageCode: QuizMessageCode, code: Int): MessageDetails

    fun getNetworkExceptionDetails(messageCode: QuizMessageCode): MessageDetails
}