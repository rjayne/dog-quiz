package au.com.jayne.dogquiz.common.network

import retrofit2.Response

sealed class ApiResponse<T> {
    companion object {
        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if(response.isSuccessful) {
                val body = response.body()
                // Empty body
                if (body == null || response.code() == 204) {
                    ApiSuccessEmptyResponse()
                } else {
                    ApiSuccessResponse(body)
                }
            } else {
                var errorBodyString: String? = null
                response.errorBody()?.string()?.let{
                    if(it.isNotBlank()) {
                        errorBodyString = it
                    }
                }

                val errorMsg = if(errorBodyString == null) {
                    if(response.message().isNullOrBlank()) {
                        "Unknown error"
                    } else {
                        response.message()
                    }
                } else {
                    null
                }
                ApiErrorResponse(errorBodyString, errorMsg)
            }
        }
    }
}

class ApiSuccessResponse<T>(val data: T): ApiResponse<T>()
class ApiSuccessEmptyResponse<T>: ApiResponse<T>()
class ApiErrorResponse<T>(val errorBody: String?, val errorMessage: String?): ApiResponse<T>()