package au.com.jayne.dogquiz.feature.game

import au.com.jayne.dogquiz.BaseTest
import au.com.jayne.dogquiz.domain.model.Dog
import au.com.jayne.dogquiz.domain.model.SubBreedResponse
import au.com.jayne.dogquiz.domain.repo.DogRepository
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

class DogInBreedGeneratorTest: BaseTest() {

    @Mock
    lateinit var mockDogRepository: DogRepository

    lateinit var dogInBreedGenerator: RandomDogListGenerator

    private val breed = "hound"

    @Before
    fun setup() {
        dogInBreedGenerator = DogInBreedGenerator(breed, mockDogRepository)
    }

    @Test
    fun testGetDogList_noDogs() = runBlockingTest {
        val subBreedResponse = SubBreedResponse(emptyArray(), "200")
        whenever(mockDogRepository.getSubBreedList(breed)).thenReturn(subBreedResponse)

        val dogList = dogInBreedGenerator.getDogList()
        Assert.assertEquals(0, dogList.size)
    }

    @Test
    fun testGetDogList() = runBlockingTest {
        setupGetSubBreedListRepoCall()

        var dogList = dogInBreedGenerator.getDogList()
        Assert.assertEquals(7, dogList.size)
        Assert.assertTrue(dogList.contains(Dog(breed, "afghan")))
        Assert.assertTrue(dogList.contains(Dog(breed, "basset")))
        Assert.assertTrue(dogList.contains(Dog(breed, "blood")))
        Assert.assertTrue(dogList.contains(Dog(breed, "english")))
        Assert.assertTrue(dogList.contains(Dog(breed, "ibizan")))
        Assert.assertTrue(dogList.contains(Dog(breed, "plott")))
        Assert.assertTrue(dogList.contains(Dog(breed, "walker")))

        // request again for list to be returned without another lookup
        dogList = dogInBreedGenerator.getDogList()
        Assert.assertEquals(7, dogList.size)

        verify(mockDogRepository, times(1)).getSubBreedList(breed)
    }

    @Test
    fun testGetRandomDogsWithExclusion() = runBlockingTest {
        val dog = Dog(breed, "english")
        setupGetSubBreedListRepoCall()

        val randomDogs = dogInBreedGenerator.getRandomDogsWithExclusion(dog)
        Assert.assertEquals(3, randomDogs.size)
        Assert.assertFalse(randomDogs.contains(dog))
    }

    private fun setupGetSubBreedListRepoCall() = runBlockingTest {
        val message: Array<String> = arrayOf("afghan",
            "basset",
            "blood",
            "english",
            "ibizan",
            "plott",
            "walker")

        val breedResponse = SubBreedResponse(message, "200")
        whenever(mockDogRepository.getSubBreedList(breed)).thenReturn(breedResponse)
    }
}