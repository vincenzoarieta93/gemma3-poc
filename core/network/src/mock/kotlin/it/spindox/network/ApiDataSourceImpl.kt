package it.spindox.network

import it.spindox.network.api.PokemonApiService
import it.spindox.network.model.AllPokemonsResponse
import it.spindox.network.model.PokemonReference
import it.spindox.result.Resource
import javax.inject.Inject

class ApiDataSourceImpl @Inject constructor(
    private val pokemonApiService: PokemonApiService
) : ApiDataSource, BaseRepo() {

    override suspend fun getAllPokemons() =
        Resource.Success(
            data = AllPokemonsResponse(
                count = 10,
                next = "next",
                previous = "previous",
                results = listOf(
                    PokemonReference(name = "Pikachu", url = ""),
                    PokemonReference(name = "Bulbasaur", url = ""),
                    PokemonReference(name = "Charmander", url = ""),
                    PokemonReference(name = "Squirtle", url = ""),
                    PokemonReference(name = "Jigglypuff", url = ""),
                    PokemonReference(name = "Meowth", url = ""),
                    PokemonReference(name = "Psyduck", url = ""),
                    PokemonReference(name = "Eevee", url = ""),
                    PokemonReference(name = "Vaporeon", url = ""),
                    PokemonReference(name = "Flareon", url = "")
                )
            )
        )
}