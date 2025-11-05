package it.spindox.network.model

import kotlinx.serialization.Serializable

@Serializable
data class PokemonFunFactResponse(
    val content: String?
)