package it.spindox.domain.usecase

import it.spindox.data.repository.abstraction.MainRepository
import javax.inject.Inject


class DeleteFavoriteItemUseCase @Inject constructor(
    private val mainRepository: MainRepository
) {
    suspend operator fun invoke(name: String) {
        mainRepository.deleteFavoritePokemon(name)
    }
}