package it.spindox.gemma3

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import dagger.hilt.android.AndroidEntryPoint
import it.spindox.data.repository.abstraction.DataStoreRepository
import it.spindox.data.repository.abstraction.InferenceModelRepository
import it.spindox.gemma3.databinding.ActivityLicenseAcknowledgmentBinding
import it.spindox.navigation.AppNavigator
import it.spindox.navigation.AppRoute
import javax.inject.Inject

@AndroidEntryPoint
class LicenseAcknowledgmentActivity : ComponentActivity() {
    private lateinit var acknowledgeButton: Button
    private lateinit var continueButton: Button
    private lateinit var binding: ActivityLicenseAcknowledgmentBinding
    @Inject
    lateinit var modelRepository: InferenceModelRepository
    @Inject
    lateinit var appNavigator: AppNavigator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLicenseAcknowledgmentBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val licenseUrl = modelRepository.getModel()?.licenseUrl.orEmpty()
        if (licenseUrl.isEmpty()) {
            Toast.makeText(this, "Missing license URL, please try again", Toast.LENGTH_LONG).show()
            appNavigator.openMainActivity(null)
            finish()
        }

        acknowledgeButton = findViewById(R.id.btnAcknowledge)
        continueButton = findViewById(R.id.btnContinue)

        // Disable "Continue" button initially
        continueButton.isEnabled = false

        acknowledgeButton.setOnClickListener {
            val customTabsIntent = CustomTabsIntent.Builder().build()
            customTabsIntent.launchUrl(this, Uri.parse(licenseUrl))

            // Enable "Continue" if user viewed license
            continueButton.isEnabled = true
        }

        continueButton.setOnClickListener {
            appNavigator.openMainActivity(AppRoute.PreparationScreen.route)
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        // Enable "Continue" if user viewed license
        // continueButton.isEnabled = true
    }
}
