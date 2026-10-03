package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notifications.MoneyFlowNotificationManager
import com.example.ui.navigation.MoneyFlowNavHost
import com.example.ui.theme.MoneyFlowTheme
import com.example.ui.viewmodel.FinanceViewModel
import com.example.ui.viewmodel.FinanceViewModelFactory

class MainActivity : FragmentActivity() {

    private var destinationSubScreen by mutableStateOf<String?>(null)
    private var destinationTab by mutableStateOf<String?>(null)

    private val viewModel: FinanceViewModel by viewModels {
        val app = application as MoneyFlowApplication
        FinanceViewModelFactory(
            app.financeRepository,
            app.userPreferencesRepository,
            app.backupRestoreManager,
            app.notificationPreferencesRepository,
            app
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleNotificationIntent(intent)

        setContent {
            val userPrefs by viewModel.userPreferences.collectAsStateWithLifecycle()

            MoneyFlowTheme(themeMode = userPrefs.themeMode) {
                MoneyFlowNavHost(
                    viewModel = viewModel,
                    onBiometricPromptRequest = { showBiometricPrompt() },
                    canUseBiometrics = checkBiometricAvailable(),
                    initialDestinationSubScreen = destinationSubScreen,
                    initialDestinationTab = destinationTab
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        if (intent == null) return
        destinationSubScreen = intent.getStringExtra(MoneyFlowNotificationManager.EXTRA_DESTINATION_SUB_SCREEN)
        destinationTab = intent.getStringExtra(MoneyFlowNotificationManager.EXTRA_DESTINATION_TAB)
    }

    private fun checkBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(this)
        return biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock MoneyFlow")
            .setSubtitle("Confirm your fingerprint or biometric credential")
            .setNegativeButtonText("Cancel")
            .build()

        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    viewModel.unlockWithBiometrics()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }
            }
        )

        biometricPrompt.authenticate(promptInfo)
    }
}
