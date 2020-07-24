package au.com.jayne.dogquiz.domain.repo

import androidx.annotation.Keep
import au.com.jayne.dogquiz.common.network.ApiErrorResponse
import au.com.jayne.dogquiz.common.network.ApiResponse
import au.com.jayne.dogquiz.common.network.ApiSuccessEmptyResponse
import au.com.jayne.dogquiz.common.network.ApiSuccessResponse
import au.com.jayne.dogquiz.domain.exception.QuizErrorException
import au.com.jayne.dogquiz.domain.exception.QuizException
import au.com.jayne.dogquiz.domain.model.MessageDetails
import au.com.jayne.dogquiz.domain.model.QuizMessageCode
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.net.SocketException

@Keep
class RepositoryAssistant(val repoErrorDetailsHandler: RepoErrorDetailsHandler) {

    /**
     * T: The response object returned by the API call.
     */
    suspend fun <T> makeApiCall(apiCall: suspend() -> Response<T>, messageCode: QuizMessageCode, errorLogMessage: String): T {
        Timber.d("makeApiCall")

        try {
            var response: Response<T> = apiCall()
            val apiResponse = ApiResponse.create(response)
            when(apiResponse) {
                is ApiSuccessResponse -> {
                    return apiResponse.data
                }
                is ApiSuccessEmptyResponse -> {
                    return handleUnexpectedSuccessfulEmptyResponse(errorLogMessage)
                }
                is ApiErrorResponse -> {
                    return handleErrorResponse(apiResponse, messageCode, errorLogMessage)
                }
            }
        } catch(ex: HttpException) {
            Timber.e(ex, "$errorLogMessage - ${ex.message()} - messageCode: $messageCode")
            throw QuizException(
                repoErrorDetailsHandler.getNetworkExceptionDetails(messageCode, ex.code())
            )
        } catch(ex: SocketException) {
            Timber.e(ex, "$errorLogMessage - ${ex.message} - messageCode: $messageCode")
            throw QuizException(
                repoErrorDetailsHandler.getUnexpectedErrorDetails(messageCode)
            )
        }
    }

    private fun <T> handleUnexpectedSuccessfulEmptyResponse(errorLogMessage: String): T {
        Timber.e("$errorLogMessage - No values were supplied, but call deemed successful. We don't expect this type of response.")
        throw QuizException(
            repoErrorDetailsHandler.getUnexpectedErrorDetails(QuizMessageCode.NO_DATA_RETURNED)
        )
    }

    private fun <T>  handleErrorResponse(apiResponse: ApiErrorResponse<T>, messageCode: QuizMessageCode, errorLogMessage: String): T {
        Timber.d("handleErrorResponse $errorLogMessage - ${apiResponse.errorMessage} ${apiResponse.errorBody}")

        apiResponse.errorBody?.let{ errorBody ->
            repoErrorDetailsHandler.handleError(errorBody, messageCode, errorLogMessage)
        }

        apiResponse.errorMessage?.let{
            throw QuizErrorException(MessageDetails(messageCode, it))
        }

        Timber.e("handleErrorResponse $errorLogMessage - ${apiResponse}")
        throw QuizException(
            repoErrorDetailsHandler.getUnexpectedErrorDetails(messageCode)
        )
    }

}

