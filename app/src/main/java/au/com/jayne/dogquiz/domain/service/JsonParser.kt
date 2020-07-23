package au.com.jayne.dogquiz.domain.service

interface JsonParser {

    fun <T> fromJson(jsonString: String, objectClass: Class<T>): T?

    fun <T> toJson(jsonObject: T, objectClass: Class<T>): String
}