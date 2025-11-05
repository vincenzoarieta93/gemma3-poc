package it.spindox.network

import it.spindox.network.api.PokemonApiService
import it.spindox.network.datasource.abstraction.ApiDataSource
import javax.inject.Inject

class ApiDataSourceImpl @Inject constructor(
    private val pokemonApiService: PokemonApiService
) : ApiDataSource, BaseRepo() {

    override suspend fun getAllPokemons() = safeApiCall { pokemonApiService.getAllPokemons() }

}