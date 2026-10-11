package com.harvestpay.app

import com.harvestpay.app.localization.AppText
import com.harvestpay.app.R
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.harvestpay.app.data.BiometricLockTiming
import com.harvestpay.app.ui.navigation.HarvestPayRoot
import com.harvestpay.app.ui.theme.HarvestPayTheme

class MainActivity : AppCompatActivity() {
    private val viewModel: HarvestViewModel by viewModels {
        HarvestViewModel.Factory(application as HarvestPayApplication)
    }
    private var biometricMessage by mutableStateOf(AppText.text(R.string.text_authenticate_to_open_harvest_pay))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AppCompatDelegate.getApplicationLocales().isEmpty) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"))
        }
        enableEdgeToEdge()
        setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            val auth by viewModel.auth.collectAsStateWithLifecycle()
            val session by viewModel.biometricSession.collectAsStateWithLifecycle()
            val autoPrompt by viewModel.biometricAutoPrompt.collectAsStateWithLifecycle()
            LaunchedEffect(auth.resolved, autoPrompt) {
                if (auth.resolved && autoPrompt) promptForUnlock()
            }
            HarvestPayTheme(settings.theme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    when {
                        !auth.resolved -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                        settings.biometricLockEnabled && session != BiometricSessionState.UNLOCKED -> {
                            Column(
                                Modifier.fillMaxSize().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Text(AppText.text(R.string.text_harvest_pay_is_locked))
                                Text(biometricMessage, modifier = Modifier.padding(vertical = 16.dp))
                                if (session == BiometricSessionState.LOCKED) {
                                    Button(onClick = ::promptForUnlock) { Text(AppText.text(R.string.text_unlock_with_biometrics)) }
                                }
                            }
                        }
                        else -> HarvestPayRoot(viewModel = viewModel, auth = auth)
                    }
                }
            }
        }
    }

    private fun authenticators(): Int? {
        val manager = BiometricManager.from(this)
        val strong = BiometricManager.Authenticators.BIOMETRIC_STRONG
        val weak = BiometricManager.Authenticators.BIOMETRIC_WEAK
        return when {
            manager.canAuthenticate(strong) == BiometricManager.BIOMETRIC_SUCCESS -> strong
            manager.canAuthenticate(weak) == BiometricManager.BIOMETRIC_SUCCESS -> weak
            else -> null
        }
    }

    private fun promptForUnlock() {
        val allowed = authenticators()
        if (allowed == null) {
            biometricMessage = AppText.text(R.string.text_biometrics_are_unavailable_set_up_a_fingerprint_or_supp)
            return
        }
        if (!viewModel.beginBiometricAuthentication()) return
        biometricMessage = AppText.text(R.string.text_authenticate_to_open_harvest_pay)
        biometricPrompt(
            allowed,
            onSuccess = viewModel::biometricAuthenticated,
            onStop = { message ->
                biometricMessage = message
                viewModel.biometricAuthenticationStopped()
            },
        )
    }

    fun requestBiometricToggle() {
        val enabled = viewModel.settings.value.biometricLockEnabled
        val allowed = authenticators()
        if (allowed == null) {
            biometricMessage = AppText.text(R.string.text_set_up_a_fingerprint_or_supported_biometric_in_android)
            android.widget.Toast.makeText(this, biometricMessage, android.widget.Toast.LENGTH_LONG).show()
            return
        }
        biometricPrompt(
            allowed,
            onSuccess = { viewModel.setBiometricLockEnabled(!enabled) },
            onStop = { message -> android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show() },
        )
    }

    fun setBiometricLockTiming(timing: BiometricLockTiming) = viewModel.setBiometricLockTiming(timing)

    private fun biometricPrompt(allowed: Int, onSuccess: () -> Unit, onStop: (String) -> Unit) {
        val prompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this), object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) { onSuccess() }
            override fun onAuthenticationFailed() { biometricMessage = AppText.text(R.string.text_biometric_not_recognized_try_again) }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) { onStop(errString.toString()) }
        })
        prompt.authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setTitle(AppText.text(R.string.text_harvest_pay_biometric_lock))
                .setSubtitle(AppText.text(R.string.text_verify_your_identity))
                .setAllowedAuthenticators(allowed)
                .setNegativeButtonText(AppText.text(R.string.text_cancel))
                .build(),
        )
    }

    override fun onStart() {
        super.onStart()
        viewModel.onForeground()
    }

    override fun onStop() {
        if (!isChangingConfigurations) viewModel.onBackground()
        super.onStop()
    }
}
