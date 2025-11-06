package it.spindox.home.main


data class MainEvent (
    val onItemClick: (String, String) -> Unit,
    val onFavoriteClick: (Int) -> Unit,
    val onThemeSwitcherClick: () -> Unit,
)

val emptyMainEvent = MainEvent ( { _, _ -> },{},{} )