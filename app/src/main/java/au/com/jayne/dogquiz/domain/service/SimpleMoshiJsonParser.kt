package au.com.jayne.dogquiz.domain.service

import com.squareup.moshi.Moshi

object SimpleMoshiJsonParser: JsonParser {

    val moshi = provideMoshi()

    private fun provideMoshi(): Moshi {
        return Moshi.Builder()
                .build()
    }

    override fun <T> fromJson(jsonString: String, objectClass: Class<T>): T? {
        return moshi.adapter<T>(objectClass).fromJson(jsonString)
    }

    override fun <T> toJson(jsonObject: T, objectClass: Class<T>): String {
        return moshi.adapter<T>(objectClass).toJson(jsonObject)
    }

}