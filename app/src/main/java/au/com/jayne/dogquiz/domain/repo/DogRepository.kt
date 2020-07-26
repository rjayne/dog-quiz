package au.com.jayne.dogquiz.domain.repo

import au.com.jayne.dogquiz.domain.model.BreedResponse
import au.com.jayne.dogquiz.domain.model.Dog
import au.com.jayne.dogquiz.domain.model.SubBreedResponse

interface DogRepository {

    suspend fun getDogList(): BreedResponse

    suspend fun getSubBreedList(breed: String): SubBreedResponse

    suspend fun getRandomImage(dog: Dog): String

}