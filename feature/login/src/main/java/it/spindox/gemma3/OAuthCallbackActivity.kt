package it.spindox.gemma3

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import it.spindox.data.repository.abstraction.DataStoreRepository
import it.spindox.data.repository.abstraction.InferenceModelRepository
import it.spindox.navigation.AppNavigator
import it.spindox.navigation.AppRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.AuthorizationService
import net.openid.appauth.GrantTypeValues
import net.openid.appauth.TokenRequest
import net.openid.appauth.TokenResponse
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

@AndroidEntryPoint
class OAuthCallbackActivity : ComponentActivity() {
    private lateinit var authService: AuthorizationService
    private val TAG = OAuthCallbackActivity::class.qualifiedName

    @Inject
    lateinit var dataStore: DataStoreRepository

    @Inject
    lateinit var modelRepository: InferenceModelRepository

    @Inject
    lateinit var appNavigator: AppNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authService = AuthorizationService(this)

        val data: Uri? = intent.data
        if (data == null) {
            Log.e(TAG, "OAuth Failed: No Data in Intent")
            finish()
            return
        }

        val authCode = data.getQueryParameter("code")
        if (authCode == null) {
            Log.e(TAG, "No Authorization Code Found")
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                delay(1000)
                val codeVerifier = dataStore.getCodeVerifier().first()

                // Create a Token Request manually
                val tokenRequest = TokenRequest.Builder(
                    AuthConfig.authServiceConfig, // Ensure this is properly set up
                    AuthConfig.clientId
                )
                    .setGrantType(GrantTypeValues.AUTHORIZATION_CODE)
                    .setAuthorizationCode(authCode)
                    .setRedirectUri(Uri.parse(AuthConfig.redirectUri))
                    .setCodeVerifier(codeVerifier) // Required for PKCE
                    .build()

                val response = suspendCancellableCoroutine<TokenResponse> { cont ->
                    authService.performTokenRequest(tokenRequest) { resp, ex ->
                        if (resp != null) {
                            cont.resume(resp) {}
                        } else {
                            cont.resumeWithException(ex ?: Exception("Unknown OAuth error"))
                        }
                    }
                }

                // Salva il token (suspend)
                dataStore.saveToken(response.accessToken.orEmpty())
                Toast.makeText(this@OAuthCallbackActivity, "Sign in succeeded", Toast.LENGTH_LONG)
                    .show()

                // Routing
                val licenseUrl = modelRepository.getModel()?.licenseUrl.orEmpty()
                if (licenseUrl.isEmpty()) {
                    appNavigator.openMainActivity(AppRoute.PreparationScreen.route)
                } else {
                    val intent = Intent(
                        this@OAuthCallbackActivity,
                        LicenseAcknowledgmentActivity::class.java
                    )
                    startActivity(intent)
                }
                finish()
            } catch (e: Exception) {
                Log.e(TAG, "OAuth error", e)
                finish()
            }
        }

    }

    override fun onDestroy() {
        authService.dispose()
        super.onDestroy()
    }
}
