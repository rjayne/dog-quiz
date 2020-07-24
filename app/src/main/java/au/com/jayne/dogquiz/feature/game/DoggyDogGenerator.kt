package au.com.jayne.dogquiz.feature.game

import au.com.jayne.dogquiz.domain.model.Dog
import au.com.jayne.dogquiz.domain.repo.DogRepository
import timber.log.Timber

class DoggyDogGenerator(private val dogRepository: DogRepository): RandomDogListGenerator {

    private  var dogsList: ArrayList<Dog>? = null

    override suspend fun getDogList(): List<Dog> {
        Timber.d("getDogList")
        if(dogsList.isNullOrEmpty()) {
            dogsList = ArrayList<Dog>()

            val dogsMap = dogRepository.getDogList().message
            dogsMap.forEach {
                val key = it.key
                if (it.value.isEmpty()) {
                    dogsList?.add(Dog(key))
                } else {
                    it.value.forEach {
                        dogsList?.add(Dog(key, it))
                    }
                }
            }

            dogsList?.shuffle()
            Timber.d("getDogList populated")
        }

        return dogsList!!
    }

    override suspend fun getRandomDogsWithExclusion(dog: Dog): List<Dog> {
        val dogsWithExclusion = getDogList().filter { !it.equals(dog) }
        return dogsWithExclusion.shuffled().subList(0,3)
    }
}