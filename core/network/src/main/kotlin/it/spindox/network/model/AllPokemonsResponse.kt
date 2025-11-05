package it.spindox.network.model

import kotlinx.serialization.Serializable

@Serializable
data class AllPokemonsResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonReference> = emptyList()
)

@Serializable
data class PokemonReference(
    val name: String,
    val url: String
)