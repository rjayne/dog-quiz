package au.com.jayne.dogquiz.domain.repo

import au.com.jayne.dogquiz.domain.model.MessageDetails

interface RepoErrorDetailsHandler {
    fun handleError(errorBody: String, messageCode: Int, errorLogMessage: String)

    fun getUnexpectedErrorDetails(messageCode: Int): MessageDetails

    fun getNetworkExceptionDetails(messageCode: Int, code: Int): MessageDetails

    fun getNetworkExceptionDetails(messageCode: Int): MessageDetails
}