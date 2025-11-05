package it.spindox.data.repository

import it.spindox.data.repository.abstraction.MainRepository
import it.spindox.network.ApiDataSource
import it.spindox.network.model.AllPokemonsResponse
import it.spindox.result.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val pokemonApiDataSource: ApiDataSource,
) : MainRepository {

    override fun getAllPokemons(): Flow<Resource<AllPokemonsResponse>> {
        return flow {
            val pokemonsResponse = pokemonApiDataSource.getAllPokemons()
            emit(pokemonsResponse)
        }
    }
}