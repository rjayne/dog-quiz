package au.com.jayne.dogquiz.domain.model

enum class QuizMessageCode(val messageCode: Int) {
    LOOKUP_FAILURE(89000),
    NO_INTERNET_CONNECTION(89010),
    NO_DATA_RETURNED(89020),
    NOT_SUPPORTED(89030),
    UNKNOWN(89040)
}