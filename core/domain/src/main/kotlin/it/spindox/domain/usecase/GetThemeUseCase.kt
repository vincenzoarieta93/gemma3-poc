package it.spindox.domain.usecase

import it.spindox.data.model.ThemeAppearance
import it.spindox.data.repository.abstraction.DataStoreRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


class GetThemeUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) {
    operator fun invoke(): StateFlow<ThemeAppearance> {
        return dataStoreRepository.getTheme()
    }
}