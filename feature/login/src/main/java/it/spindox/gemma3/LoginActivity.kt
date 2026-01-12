package it.spindox.gemma3

import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import it.spindox.data.repository.abstraction.DataStoreRepository
import it.spindox.gemma3.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.ResponseTypeValues
import java.security.MessageDigest
import java.security.SecureRandom
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    private lateinit var authService: AuthorizationService
    private lateinit var codeVerifier: String
    private lateinit var codeChallenge: String
    private lateinit var binding: ActivityLoginBinding

    @Inject
    lateinit var datastore: DataStoreRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authService = AuthorizationService(this)

        binding.btnLogin.setOnClickListener {
            loginWithHuggingFace() // Start OAuth login when button is clicked
        }
    }

    private fun loginWithHuggingFace() {
        lifecycleScope.launch {
            try {
                // Generate PKCE parameters
                codeVerifier = generateCodeVerifier()
                codeChallenge = generateCodeChallenge(codeVerifier)

                // Save the code verifier securely (suspend function)
                datastore.saveCodeVerifier(codeVerifier)

                // Build the authorization request
                val authRequest = AuthorizationRequest.Builder(
                    AuthConfig.authServiceConfig,
                    AuthConfig.clientId,
                    ResponseTypeValues.CODE,
                    Uri.parse(AuthConfig.redirectUri)
                )
                    .setScope("read-repos") // Adjust scopes if needed
                    .setCodeVerifier(codeVerifier, codeChallenge, "S256")
                    .build()

                // Launch the OAuth login page
                val authIntent = authService.getAuthorizationRequestIntent(authRequest)
                startActivity(authIntent)
                finish()
            } catch (e: Exception) {
                Log.e("LoginActivity", "Error during HuggingFace login", e)
                Toast.makeText(
                    this@LoginActivity,
                    "Login failed. Please try again.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun generateCodeVerifier(): String {
        val random = ByteArray(32)
        SecureRandom().nextBytes(random)
        return Base64.encodeToString(random, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
    }

    private fun generateCodeChallenge(codeVerifier: String): String {
        val bytes = codeVerifier.toByteArray()
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
    }

    override fun onDestroy() {
        authService.dispose()
        super.onDestroy()
    }
}
