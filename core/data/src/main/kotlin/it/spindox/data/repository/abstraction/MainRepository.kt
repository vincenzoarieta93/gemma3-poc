package it.spindox.data.repository.abstraction

import it.spindox.data.model.Pokemon
import it.spindox.result.Resource
import kotlinx.coroutines.flow.Flow

interface MainRepository {
    fun getAllPokemons(): Flow<Resource<List<Pokemon>>>
    fun getFavoritePokemons(): Flow<List<Pokemon>>
    suspend fun addFavoritePokemon(name: String)
    suspend fun deleteFavoritePokemon(name: String)
}