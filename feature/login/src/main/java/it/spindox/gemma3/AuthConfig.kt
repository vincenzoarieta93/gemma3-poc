package it.spindox.gemma3

import net.openid.appauth.AuthorizationServiceConfiguration
import android.net.Uri

object AuthConfig {
  const val clientId = "c140e815-be10-4942-943d-b95a279c289f" // Hugging Face Client ID
  const val redirectUri = "it.spindox.gemma3://oauth2callback"

  // OAuth 2.0 Endpoints (Authorization + Token Exchange)
  private const val authEndpoint = "https://huggingface.co/oauth/authorize"
  private const val tokenEndpoint = "https://huggingface.co/oauth/token"

  // OAuth service configuration (AppAuth library requires this)
  val authServiceConfig = AuthorizationServiceConfiguration(
    Uri.parse(authEndpoint), // Authorization endpoint
    Uri.parse(tokenEndpoint) // Token exchange endpoint
  )
}
