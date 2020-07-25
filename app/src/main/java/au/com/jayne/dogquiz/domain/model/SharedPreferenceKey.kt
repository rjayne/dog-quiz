package au.com.jayne.dogquiz.domain.model

enum class SharedPreferenceKey(val keyName: String) {
    SOUNDS_ENABLED("soundsEnabled"),
    GAME_SCORES("gameScores"),
    LAST_NAME_USED("lastNameUsed")
}