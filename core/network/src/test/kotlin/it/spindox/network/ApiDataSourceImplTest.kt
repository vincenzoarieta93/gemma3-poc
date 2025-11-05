package it.spindox.network

import it.spindox.network.api.PokemonApiService
import it.spindox.network.model.AllPokemonsResponse
import it.spindox.result.Resource
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiDataSourceImplTest {

    private lateinit var server: MockWebServer // Fake server from square lib
    private lateinit var api: PokemonApiService

    @Before
    fun beforeEach() {
        server = MockWebServer()
        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokemonApiService::class.java)
    }

    @Test
    fun getAllPokemons() = runBlocking {
        // Arrange: Prepare mock response
        val mockResponse = MockResponse().setBody(
            """{
  "count": 1302,
  "next": "https://pokeapi.co/api/v2/pokemon?offset=20&limit=20",
  "previous": null,
  "results": [
    {
      "name": "bulbasaur",
      "url": "https://pokeapi.co/api/v2/pokemon/1/"
    },
    {
      "name": "ivysaur",
      "url": "https://pokeapi.co/api/v2/pokemon/2/"
    },
    {
      "name": "venusaur",
      "url": "https://pokeapi.co/api/v2/pokemon/3/"
    },
    {
      "name": "charmander",
      "url": "https://pokeapi.co/api/v2/pokemon/4/"
    },
    {
      "name": "charmeleon",
      "url": "https://pokeapi.co/api/v2/pokemon/5/"
    },
    {
      "name": "charizard",
      "url": "https://pokeapi.co/api/v2/pokemon/6/"
    },
    {
      "name": "squirtle",
      "url": "https://pokeapi.co/api/v2/pokemon/7/"
    },
    {
      "name": "wartortle",
      "url": "https://pokeapi.co/api/v2/pokemon/8/"
    },
    {
      "name": "blastoise",
      "url": "https://pokeapi.co/api/v2/pokemon/9/"
    },
    {
      "name": "caterpie",
      "url": "https://pokeapi.co/api/v2/pokemon/10/"
    },
    {
      "name": "metapod",
      "url": "https://pokeapi.co/api/v2/pokemon/11/"
    },
    {
      "name": "butterfree",
      "url": "https://pokeapi.co/api/v2/pokemon/12/"
    },
    {
      "name": "weedle",
      "url": "https://pokeapi.co/api/v2/pokemon/13/"
    },
    {
      "name": "kakuna",
      "url": "https://pokeapi.co/api/v2/pokemon/14/"
    },
    {
      "name": "beedrill",
      "url": "https://pokeapi.co/api/v2/pokemon/15/"
    },
    {
      "name": "pidgey",
      "url": "https://pokeapi.co/api/v2/pokemon/16/"
    },
    {
      "name": "pidgeotto",
      "url": "https://pokeapi.co/api/v2/pokemon/17/"
    },
    {
      "name": "pidgeot",
      "url": "https://pokeapi.co/api/v2/pokemon/18/"
    },
    {
      "name": "rattata",
      "url": "https://pokeapi.co/api/v2/pokemon/19/"
    },
    {
      "name": "raticate",
      "url": "https://pokeapi.co/api/v2/pokemon/20/"
    }
  ]
}"""
        ).setResponseCode(200)
        server.enqueue(mockResponse)

        val response = api.getAllPokemons().body()
        delay(100)
        assertNotNull(response)
        assertEquals(20, response?.results?.size)
        assertEquals("bulbasaur", response?.results?.firstOrNull()?.name)
    }/*

    @Test
    fun getAllPokemonsError() = runBlocking {
        val mockErrorResponse = MockResponse().setResponseCode(404)
        server.enqueue(mockErrorResponse)

        // Act: Call the API
        val response = api.getAllPokemons()
        delay(100)
        server.enqueue(mockErrorResponse)
        // Assert: Verify that we received an error response
        assertTrue(response is Resource.Error<*>)

        if (response is Resource.Error<*>) {
            assertEquals("404", response.message)  // Assuming your Resource.Error uses the response code as a message
        } else {
            fail("Expected a Resource.Error but got: $response")
        }
    }*/
}