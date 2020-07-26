package au.com.jayne.dogquiz.feature.game

import au.com.jayne.dogquiz.BaseTest
import au.com.jayne.dogquiz.domain.model.BreedResponse
import au.com.jayne.dogquiz.domain.model.Dog
import au.com.jayne.dogquiz.domain.repo.DogRepository
import au.com.jayne.dogquiz.domain.repo.DogRepositoryImpl
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class DoggyDogGeneratorTest: BaseTest() {

    @Mock
    lateinit var mockDogRepository: DogRepository

    lateinit var doggyDogGenerator: DoggyDogGenerator

    @Before
    fun setup() {
        doggyDogGenerator = DoggyDogGenerator(mockDogRepository)
    }

    @Test
    fun testGetDogList_noDogs() = runBlockingTest {
        val message = HashMap<String, Array<String>>()

        val breedResponse = BreedResponse(message, "200")
        whenever(mockDogRepository.getDogList()).thenReturn(breedResponse)

        val dogList = doggyDogGenerator.getDogList()
        Assert.assertEquals(0, dogList.size)
    }

    @Test
    fun testGetDogList() = runBlockingTest {
        setupGetDogListRepoCall()

        var dogList = doggyDogGenerator.getDogList()
        Assert.assertEquals(5, dogList.size)
        Assert.assertTrue(dogList.contains(Dog("australian", "shepherd")))
        Assert.assertTrue(dogList.contains(Dog("beagle")))
        Assert.assertTrue(dogList.contains(Dog("bulldog", "boston")))
        Assert.assertTrue(dogList.contains(Dog("bulldog", "english")))
        Assert.assertTrue(dogList.contains(Dog("bulldog", "french")))

        // request again for list to be returned without another lookup
        dogList = doggyDogGenerator.getDogList()
        Assert.assertEquals(5, dogList.size)

        verify(mockDogRepository, times(1)).getDogList()
    }

    @Test
    fun testGetRandomDogsWithExclusion() = runBlockingTest {
        val dog = Dog("australian", "shepherd")
        setupGetDogListRepoCall()

        val randomDogs = doggyDogGenerator.getRandomDogsWithExclusion(dog)
        Assert.assertEquals(3, randomDogs.size)
        Assert.assertFalse(randomDogs.contains(dog))
    }

    private fun setupGetDogListRepoCall() = runBlockingTest {
        val message = HashMap<String, Array<String>>()
        message.put("australian", arrayOf("shepherd"))
        message.put("beagle", emptyArray())
        message.put("bulldog", arrayOf("boston", "english", "french"))

        val breedResponse = BreedResponse(message, "200")
        whenever(mockDogRepository.getDogList()).thenReturn(breedResponse)
    }
}