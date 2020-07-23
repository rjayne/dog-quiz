package au.com.jayne.dogquiz.domain.repo

import au.com.jayne.dogquiz.common.util.ResourceProvider
import au.com.jayne.dogquiz.domain.model.BreedResponse
import au.com.jayne.dogquiz.domain.model.Dog
import au.com.jayne.dogquiz.domain.model.QuizMessageCode
import au.com.jayne.dogquiz.domain.model.SubBreedResponse
import au.com.jayne.dogquiz.domain.service.DogApiService

class DogRepository(resourceProvider: ResourceProvider, private val dogApiService: DogApiService) {

    private val repositoryAssistant = RepositoryAssistant(DogRepoErrorDetailsHandler(resourceProvider))

    suspend fun getDogList(): BreedResponse {
        return repositoryAssistant.makeApiCall(
            suspend { dogApiService.getDogList() },
            QuizMessageCode.LOOKUP_FAILURE.messageCode,
            "Failed to retrieve all dogs list"
        )
    }

    suspend fun getSubBreedList(breed: String): SubBreedResponse {
        return repositoryAssistant.makeApiCall(
            suspend { dogApiService.getSubBreedList(breed) },
            QuizMessageCode.LOOKUP_FAILURE.messageCode,
            "Failed to retrieve sub breeds for $breed"
        )
    }

    suspend fun getRandomImage(dog: Dog): String {
        if(dog.subBreed.isNullOrBlank()) {
            return getRandomImageForBreed(dog.breed)
        } else {
            return getRandomImageForSubBreed(dog.breed, dog.subBreed)
        }
    }

    private suspend fun getRandomImageForBreed(breed: String): String {
        return repositoryAssistant.makeApiCall(
            suspend { dogApiService.getRandomImageForBreed(breed) },
            QuizMessageCode.LOOKUP_FAILURE.messageCode,
            "Failed to retrieve image for $breed"
        ).url
    }

    private suspend fun getRandomImageForSubBreed(breed: String, subBreed: String): String {
        return repositoryAssistant.makeApiCall(
            suspend { dogApiService.getRandomImageForSubBreed(breed, subBreed) },
            QuizMessageCode.LOOKUP_FAILURE.messageCode,
            "Failed to retrieve image for $breed $subBreed"
        ).url
    }
}