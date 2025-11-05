package it.spindox.network.api

import it.spindox.network.model.AllPokemonsResponse
import retrofit2.Response
import retrofit2.http.GET

/**
 * Interface that describes REST Requests
 * GET -> Retrieve Data
 * POST -> Send data to server and create new resources
 * PUT -> Update an existing resource
 * DELETE -> delete a resource
 */
interface PokemonApiService {

    @GET("api/v2/pokemon?limit=151&offset=0")
    suspend fun getAllPokemons(): Response<AllPokemonsResponse>
}