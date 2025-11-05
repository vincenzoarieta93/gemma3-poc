package it.spindox.network.datasource.abstraction

import it.spindox.network.model.AllPokemonsResponse
import it.spindox.result.Resource

interface ApiDataSource {

    suspend fun getAllPokemons(): Resource<AllPokemonsResponse>

}