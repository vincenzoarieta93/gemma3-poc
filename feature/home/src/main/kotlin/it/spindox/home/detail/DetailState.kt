package it.spindox.home.detail

import it.spindox.data.model.PokemonFunFact

data class DetailScreenState (
    val detailState: DetailState,
    val funFactState: FunFactUiState,
)

data class DetailState (
    val isLoading: Boolean = false,
    val name: String = "",
)

sealed interface FunFactUiState {
    object Loading : FunFactUiState
    data class Success(val funFact: PokemonFunFact) : FunFactUiState
    data class Error(val message: String) : FunFactUiState
}

/*
 * Sample data
 */
val sampleDetailState = DetailState (
    isLoading = false,
    name = "Charizard",
)

val sampleDetailScreenState = DetailScreenState (
    detailState = sampleDetailState,
    funFactState = FunFactUiState.Loading
)