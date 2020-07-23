package au.com.jayne.dogquiz.feature.game

import au.com.jayne.dogquiz.domain.model.Dog

interface RandomDogListGenerator {

    suspend fun getDogList(): List<Dog>

    suspend fun getRandomDogsWithExclusion(dog: Dog): List<Dog>

}