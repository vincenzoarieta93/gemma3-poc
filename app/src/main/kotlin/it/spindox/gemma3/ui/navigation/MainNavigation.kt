package it.spindox.gemma3.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import it.spindox.home.detail.DetailPage
import it.spindox.home.main.MainPage
import kotlinx.serialization.Serializable

@Serializable
object Main

@Serializable
data class Detail(val name: String, val url: String)

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Main
    ) {
        composable<Main> {
            MainPage(
                onGoToDetails = { name, url ->
                    navController.navigate(Detail(name, url))
                }
            )
        }
        composable<Detail> { backStackEntry ->
            val detail: Detail = backStackEntry.toRoute()
            DetailPage(
                name = detail.name,
                detailsUrl = detail.url,
                onGoBack = { navController.popBackStack() }
            )
        }
    }
}