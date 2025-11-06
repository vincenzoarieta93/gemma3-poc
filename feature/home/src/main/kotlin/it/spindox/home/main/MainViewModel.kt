package it.spindox.home.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.spindox.coroutine.DefaultDispatcherProvider
import it.spindox.domain.usecase.AddFavoriteItemUseCase
import it.spindox.domain.usecase.DeleteFavoriteItemUseCase
import it.spindox.domain.usecase.GetAllPokemonsUseCase
import it.spindox.domain.usecase.GetFavoriteItemsUseCase
import it.spindox.domain.usecase.GetThemeUseCase
import it.spindox.domain.usecase.SetThemeUseCase
import it.spindox.result.Resource
import it.spindox.result.loading
import it.spindox.result.success
import it.spindox.data.model.ThemeAppearance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dispatcherProvider: DefaultDispatcherProvider,
    private val getAllPokemonsUseCase: GetAllPokemonsUseCase,
    private val getFavoriteItemsUseCase: GetFavoriteItemsUseCase,
    private val addFavoriteItemUseCase: AddFavoriteItemUseCase,
    private val deleteFavoriteItemUseCase: DeleteFavoriteItemUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val getThemeUseCase: GetThemeUseCase,
) : ViewModel() {

    private val _uiState by lazy { MutableStateFlow(MainState()) }
    val uiState: StateFlow<MainState> = _uiState.asStateFlow()

    val event = MainEvent (
        onItemClick = { _, _ -> },
        onThemeSwitcherClick = { toggleTheme() },
        onFavoriteClick = { addFavoriteItem(it) }
    )

    init {
        getTheme()
        getAllPokemons()
    }

    private fun getTheme() {
        viewModelScope.launch {
            getThemeUseCase().collectLatest { theme ->
                _uiState.update { it.copy(isDarkTheme = theme == ThemeAppearance.DARK) }
            }
        }
    }

    private fun addFavoriteItem(index: Int) {
        _uiState.value.pokemonList.let {
            when(it) {
                is Resource.Success -> {
                    viewModelScope.launch {
                        if(it.data[index].isFavorite)
                            deleteFavoriteItemUseCase( it.data[index].name )
                        else
                            addFavoriteItemUseCase( it.data[index].name )
                    }
                }
                is Resource.Error -> { /* Do nothing */ }
                is Resource.Loading -> { /* Do nothing */ }
            }
        }
    }

    private fun getAllPokemons() = viewModelScope.launch(dispatcherProvider.io) {

        combine(getAllPokemonsUseCase(), getFavoriteItemsUseCase()) { allItems, favoriteItems ->
            allItems to favoriteItems
        }.collectLatest { (allItems, favoriteItems) ->
            _uiState.update { oldState -> oldState.copy(
                pokemonList = when(allItems) {
                    is Resource.Success -> success {
                        allItems.data.map { item ->
                            ItemState(
                                name = item.name,
                                url = item.url,
                                isFavorite = favoriteItems.any { it.name == item.name }
                            )
                        }
                    }
                    is Resource.Error -> error(message = allItems.message)
                    is Resource.Loading -> loading()
                }
            ) }
        }

    }

    private fun toggleTheme() {
        viewModelScope.launch {
            setThemeUseCase(
                if(_uiState.value.isDarkTheme) ThemeAppearance.LIGHT else ThemeAppearance.DARK
            )
        }
    }
}