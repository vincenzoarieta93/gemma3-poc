package it.spindox.navigation

interface AppNavigator {
    companion object {
        const val NAVIGATE_TO_KEY: String = "NAVIGATE_TO"
    }
    fun openMainActivity(navigateTo: String?)
}