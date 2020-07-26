package au.com.jayne.dogquiz.domain.repo

import au.com.jayne.dogquiz.common.util.ResourceProvider
import au.com.jayne.dogquiz.domain.model.BreedResponse
import au.com.jayne.dogquiz.domain.model.Dog
import au.com.jayne.dogquiz.domain.model.QuizMessageCode
import au.com.jayne.dogquiz.domain.model.SubBreedResponse
import au.com.jayne.dogquiz.domain.service.DogApiService

class DogRepositoryImpl(resourceProvider: ResourceProvider, private val dogApiService: DogApiService): DogRepository {

    private val repositoryAssistant = RepositoryAssistant(DogRepoErrorDetailsHandler(resourceProvider))

    override suspend fun getDogList(): BreedResponse {
        return repositoryAssistant.makeApiCall(
            suspend { dogApiService.getDogList() },
            QuizMessageCode.ALL_DOGS_LOOKUP_FAILURE,
            "Failed to retrieve all dogs list"
        )
    }

    override suspend fun getSubBreedList(breed: String): SubBreedResponse {
        return repositoryAssistant.makeApiCall(
            suspend { dogApiService.getSubBreedList(breed) },
            QuizMessageCode.LOOKUP_FAILURE,
            "Failed to retrieve sub breeds for $breed"
        )
    }

    override suspend fun getRandomImage(dog: Dog): String {
        if(dog.subBreed.isNullOrBlank()) {
            return getRandomImageForBreed(dog.breed)
        } else {
            return getRandomImageForSubBreed(dog.breed, dog.subBreed)
        }
    }

    private suspend fun getRandomImageForBreed(breed: String): String {
        return repositoryAssistant.makeApiCall(
            suspend { dogApiService.getRandomImageForBreed(breed) },
            QuizMessageCode.LOOKUP_FAILURE,
            "Failed to retrieve image for $breed"
        ).url
    }

    private suspend fun getRandomImageForSubBreed(breed: String, subBreed: String): String {
        return repositoryAssistant.makeApiCall(
            suspend { dogApiService.getRandomImageForSubBreed(breed, subBreed) },
            QuizMessageCode.LOOKUP_FAILURE,
            "Failed to retrieve image for $breed $subBreed"
        ).url
    }
}