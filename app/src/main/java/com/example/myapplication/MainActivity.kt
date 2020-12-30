package com.example.myapplication

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor


class MainActivity : AppCompatActivity() {

    private lateinit var tvInfo: TextView

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    @SuppressLint("SetTextI18n")
    fun showLogText(message: String) {
        tvInfo.text = tvInfo.text.toString() + "\n" + message
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvInfo = findViewById(R.id.tvInfo)

        if ((getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager).isDeviceSecure) {
            showLogText("Device is secure")
        } else {
            showLogText("Device is not secure")
        }
        isAvailableAuth()
    }

    private fun isAvailableAuth() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {

            BiometricManager.BIOMETRIC_SUCCESS -> {
                showLogText("App can authenticate using biometrics.")
                startBiometricAuth()
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                showLogText("No biometric features available on this device.")

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                showLogText("Biometric features are currently unavailable.")

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                showLogText("BIOMETRIC_ERROR_NONE_ENROLLED")
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                showLogText("BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED")
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                showLogText("BIOMETRIC_ERROR_UNSUPPORTED")
            }
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                showLogText("BIOMETRIC_STATUS_UNKNOWN")
            }
        }
    }

    private fun startBiometricAuth() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    showLogText("Authentication error: $errString")
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    showLogText("Authentication succeeded!")
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showLogText("Authentication failed")
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Cancel") // Only Biometrics Auth
            .setConfirmationRequired(false)
            .build()

        findViewById<Button>(R.id.btnAuth).setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    /*private fun tryDeprecatedAPI() {
        val fingerprintManager = FingerprintManagerCompat.from(this)

        if (fingerprintManager.isHardwareDetected) {
            showLogText("Fingerprint scanner has been found")

            if (fingerprintManager.hasEnrolledFingerprints()) {
                showLogText("There is enrolled fingerprint... trying to auth")
            } else {
                showLogText("There is no enrolled fingerprint")
            }
        } else {
            showLogText("There is no fingerprint scanner")
        }
    }*/
}