package com.example.data.repository

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.KeyGenerator
import javax.crypto.Mac

object SecurityManager {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "MoneyFlowSecurityKey"

    init {
        try {
            ensureKeyExists()
        } catch (_: Exception) {}
    }

    private fun ensureKeyExists() {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_HMAC_SHA256,
                ANDROID_KEYSTORE
            )
            val spec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_SIGN
            ).build()
            keyGenerator.init(spec)
            keyGenerator.generateKey()
        }
    }

    fun hashPin(pin: String): String {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            val secretKey = keyStore.getKey(KEY_ALIAS, null)
            if (secretKey != null) {
                val mac = Mac.getInstance("HmacSHA256")
                mac.init(secretKey)
                val bytes = mac.doFinal(pin.toByteArray(Charsets.UTF_8))
                bytes.joinToString("") { "%02x".format(it) }
            } else {
                fallbackHash(pin)
            }
        } catch (_: Exception) {
            fallbackHash(pin)
        }
    }

    private fun fallbackHash(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(("moneyflow_salt_" + pin).toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyPin(enteredPin: String, storedHash: String): Boolean {
        if (storedHash.isBlank()) return true
        val computedHash = hashPin(enteredPin)
        return computedHash == storedHash
    }
}
