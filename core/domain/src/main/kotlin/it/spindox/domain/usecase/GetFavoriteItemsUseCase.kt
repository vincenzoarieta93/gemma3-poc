package it.spindox.domain.usecase

import it.spindox.coroutine.DefaultDispatcherProvider
import it.spindox.data.model.Pokemon
import it.spindox.data.repository.abstraction.MainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class GetFavoriteItemsUseCase @Inject constructor(
    private val dispatcherProvider: DefaultDispatcherProvider,
    private val mainRepository: MainRepository
) {
    operator fun invoke(): Flow<List<Pokemon>> =
        mainRepository.getFavoritePokemons().flowOn(dispatcherProvider.io)

}