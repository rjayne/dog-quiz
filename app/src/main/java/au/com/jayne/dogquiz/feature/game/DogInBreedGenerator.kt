package au.com.jayne.dogquiz.feature.game

import au.com.jayne.dogquiz.domain.model.Dog
import au.com.jayne.dogquiz.domain.repo.DogRepository

class DogInBreedGenerator(val breed: String, private val dogRepository: DogRepository): RandomDogListGenerator {

    private var dogsList: ArrayList<Dog>? = null

    override suspend fun getDogList(): List<Dog> {
        if(dogsList.isNullOrEmpty()) {
            dogsList = ArrayList<Dog>()

            val subBreeds = dogRepository.getSubBreedList(breed).message
            subBreeds.forEach {
                dogsList?.add(Dog(breed, it))
            }

            dogsList?.shuffle()
        }
        return dogsList!!
    }

    override suspend fun getRandomDogsWithExclusion(dog: Dog): List<Dog> {
        val dogsWithExclusion = getDogList().filter { !it.equals(dog) }
        return dogsWithExclusion.shuffled().subList(0,3)
    }
}