package it.spindox.domain.usecase

import it.spindox.data.model.ThemeAppearance
import it.spindox.data.repository.abstraction.DataStoreRepository
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke(theme: ThemeAppearance) {
        dataStoreRepository.setTheme(theme)
    }
}