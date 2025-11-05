package it.spindox.data.repository

import it.spindox.data.model.Pokemon
import it.spindox.data.repository.abstraction.MainRepository
import it.spindox.database.datasource.abstraction.SampleDatabaseDataSource
import it.spindox.database.model.SampleEntity
import it.spindox.network.datasource.abstraction.ApiDataSource
import it.spindox.network.model.AllPokemonsResponse
import it.spindox.network.model.PokemonReference
import it.spindox.result.Resource
import it.spindox.result.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class MainRepositoryImpl @Inject constructor(
    private val apiDataSource: ApiDataSource,
    private val dbDataSource: SampleDatabaseDataSource,
) : MainRepository {

    override fun getAllPokemons(): Flow<Resource<List<Pokemon>>> {
        return flow {
            val pokemonsResponse = apiDataSource.getAllPokemons().map { it.toDataModel() }
            emit(pokemonsResponse)
        }
    }

    override fun getFavoritePokemons(): Flow<List<Pokemon>> {
        return dbDataSource.getAllItems().map { list ->
            list.map {
                it.toDataModel()
            }
        }
    }

    override suspend fun addFavoritePokemon(name: String) {
        dbDataSource.insertItem(name.toDbEntity())
    }

    override suspend fun deleteFavoritePokemon(name: String) {
        dbDataSource.deleteItem(name)
    }

}

private fun AllPokemonsResponse.toDataModel() =
    this.results.map { it.toDataModel() }

private fun PokemonReference.toDataModel() = Pokemon (
    name = this.name,
    url = this.url,
)

private fun SampleEntity.toDataModel() = Pokemon (
    name = this.name,
    url = "",
)

private fun String.toDbEntity() = SampleEntity (
    name = this
)

private fun Pokemon.toDbEntity() = SampleEntity (
    name = this.name
)