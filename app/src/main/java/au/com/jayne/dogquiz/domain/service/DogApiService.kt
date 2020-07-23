package au.com.jayne.dogquiz.domain.service

import au.com.jayne.dogquiz.domain.model.BreedResponse
import au.com.jayne.dogquiz.domain.model.ImageResponse
import au.com.jayne.dogquiz.domain.model.SubBreedResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DogApiService {

    @GET("breeds/list/all")
    suspend fun getDogList(): Response<BreedResponse>

    @GET("breed/{breed}/list")
    suspend fun getSubBreedList(@Path("breed") breed: String): Response<SubBreedResponse>

    @GET("breed/{breed}/images/random")
    suspend fun getRandomImageForBreed(@Path("breed") breed: String): Response<ImageResponse>

    @GET("breed/{breed}/{subbreed}/images/random")
    suspend fun getRandomImageForSubBreed(@Path("breed") breed: String, @Path("subbreed") subbreed: String): Response<ImageResponse>
}