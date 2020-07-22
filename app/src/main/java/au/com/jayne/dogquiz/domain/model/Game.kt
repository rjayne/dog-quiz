package au.com.jayne.dogquiz.domain.model

import au.com.jayne.dogquiz.R

enum class Game(val nameResId: Int) {
    HOUND_QUIZ(R.string.game_hound_name),
    SPANIEL_QUIZ(R.string.game_spaniel_name),
    TERRIER_QUIZ(R.string.game_terrier_name),
    DOGGY_QUIZ(R.string.game_doggy_name)
}