package au.com.jayne.dogquiz.domain.model

enum class SharedPreferenceKey(val keyName: String) {
    SOUNDS_ENABLE("SoundsEnabled"),
    HIGH_SCORES("highScores")
}