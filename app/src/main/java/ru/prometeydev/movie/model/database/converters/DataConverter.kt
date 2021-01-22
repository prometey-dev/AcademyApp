package ru.prometeydev.movie.model.database.converters

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import ru.prometeydev.movie.model.network.dto.ActorDto
import ru.prometeydev.movie.model.network.dto.GenreDto
import java.lang.reflect.Type

class DataConverter {

    private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @TypeConverter
    fun listGenresToJson(value: List<GenreDto>): String = toJson(value)

    @TypeConverter
    fun jsonToListGenres(value: String): List<GenreDto>? = fromJson(value)

    @TypeConverter
    fun listActorsToJson(value: List<ActorDto>): String = toJson(value)

    @TypeConverter
    fun jsonToListActors(value: String): List<ActorDto>? = fromJson(value)

    private inline fun <reified T> fromJson(value: String): List<T>? {
        val type: Type = Types.newParameterizedType(List::class.java, T::class.java)
        val jsonAdapter = moshi.adapter<List<T>>(type)
        return jsonAdapter.fromJson(value)
    }

    private inline fun <reified T> toJson(value: List<T>?): String {
        val type: Type = Types.newParameterizedType(List::class.java, T::class.java)
        val jsonAdapter = moshi.adapter<List<T>>(type)
        return jsonAdapter.toJson(value)
    }

}