package au.com.jayne.dogquiz.domain.model

data class Dog(val breed: String, val subBreed: String? = null) {
    fun getName(): String {
        subBreed?.let{
            return "${it.capitalize()} ${breed.capitalize()}"
        }
        return breed.capitalize()
    }
}

data class DogChallenge(val dog: Dog, val imageUrl: String, val dogSelection: List<Dog>)