package it.spindox.gemma3

import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import it.spindox.navigation.AppNavigator
import javax.inject.Inject

class DefaultAppNavigator @Inject constructor(
    private val context: Context
) : AppNavigator {
    override fun openMainActivity(navigateTo: String?) {
        context.startActivity(
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                navigateTo.takeIf { !it.isNullOrBlank() }?.let {
                    putExtra(AppNavigator.NAVIGATE_TO_KEY, it)
                }
            }
        )
    }
}