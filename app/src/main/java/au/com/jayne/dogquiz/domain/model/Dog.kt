package au.com.jayne.dogquiz.domain.model

data class Dog(val breed: String, val subBreed: String? = null) {
    fun getName(): String {
        subBreed?.let{
            return "$it $breed".capitalize()
        }
        return breed
    }
}

data class DogChallenge(val dog: Dog, val imageUrl: String, val dogSelection: List<Dog>)