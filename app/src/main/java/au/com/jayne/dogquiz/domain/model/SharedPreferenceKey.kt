package au.com.jayne.dogquiz.domain.model

enum class SharedPreferenceKey(val keyName: String) {
    SOUNDS_ENABLED("soundsEnabled"),
    HIGH_SCORES("highScores")
}