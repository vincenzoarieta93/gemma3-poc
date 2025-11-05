package it.spindox.domain.usecase

import it.spindox.coroutine.DefaultDispatcherProvider
import it.spindox.data.model.Pokemon
import it.spindox.data.repository.abstraction.MainRepository
import it.spindox.result.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetAllPokemonsUseCase @Inject constructor(
    private val dispatcherProvider: DefaultDispatcherProvider,
    private val mainRepository: MainRepository
) {
    operator fun invoke(): Flow<Resource<List<Pokemon>>> =
        mainRepository.getAllPokemons().flowOn(dispatcherProvider.io)

}