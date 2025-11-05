package it.spindox.home.main

import it.spindox.result.Resource
import it.spindox.result.success


data class MainState (
    val pokemonList: Resource<List<ItemState>> = success { emptyList() },
    val isDarkTheme: Boolean = false,
)

data class ItemState (
    val name: String,
    val isFavorite: Boolean,
)

val sampleMainState = MainState (
    pokemonList = success {
        listOf(
            ItemState(name = "bulbasaur", isFavorite = false),
            ItemState(name = "charmender", isFavorite = true),
            ItemState(name = "squirtle", isFavorite = false),
            ItemState(name = "spindox", isFavorite = false),
        )
    },
)