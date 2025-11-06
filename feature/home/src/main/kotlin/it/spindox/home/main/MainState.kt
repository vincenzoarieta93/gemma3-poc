package it.spindox.home.main

import it.spindox.result.Resource
import it.spindox.result.success


data class MainState (
    val pokemonList: Resource<List<ItemState>> = success { emptyList() },
    val isDarkTheme: Boolean = false,
)

data class ItemState (
    val name: String,
    val url: String,
    val isFavorite: Boolean,
)

val sampleMainState = MainState (
    pokemonList = success {
        listOf(
            ItemState(name = "bulbasaur", "https://pokeapi.co/api/v2/pokemon/1", isFavorite = false),
            ItemState(name = "charmender", "https://pokeapi.co/api/v2/pokemon/4/", isFavorite = true),
            ItemState(name = "squirtle", "https://pokeapi.co/api/v2/pokemon/7/", isFavorite = false),
            ItemState(name = "ivysaur", "https://pokeapi.co/api/v2/pokemon/2/", isFavorite = false),
        )
    },
)