package it.spindox.datastore.utility

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProtection
import android.util.Base64
import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class CryptoManager {

    companion object {
        private const val KEYSTORE = "AndroidKeyStore"
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
        private const val DIVIDER = "|||"

        private const val ALIAS = "crypto_secrets"

        private const val KEYGEN_SIZE = 256

        private const val TAG = "CryptoManager"
    }

    private val keyStore = KeyStore.getInstance(KEYSTORE).apply {
        load(null)
    }

    private fun getEncryptCipher(): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getKey())
        }
    }

    private fun getDecryptCipherForIv(iv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
        }
    }

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry(ALIAS, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: generateSecretKey()
    }

    private fun generateSecretKey(): SecretKey {

        val keyGen = KeyGenerator.getInstance(ALGORITHM)
        keyGen.init(KEYGEN_SIZE)

        val secretKey: SecretKey = keyGen.generateKey()

        val entry = KeyStore.SecretKeyEntry(secretKey)
        val protectionParameter =
            KeyProtection.Builder(KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(BLOCK_MODE)
                .setEncryptionPaddings(PADDING)
                .build()

        keyStore.setEntry(ALIAS, entry, protectionParameter)

        return secretKey
    }

    fun encrypt(bytes: ByteArray): String? {
        return try {
            val cipher = getEncryptCipher()
            val cipherText = Base64.encodeToString(cipher.doFinal(bytes), Base64.DEFAULT)
            val iv = Base64.encodeToString(cipher.iv, Base64.DEFAULT)

            "${cipherText}$DIVIDER$iv"
        } catch (e: Exception) {
            Log.e(TAG, "encrypt: error msg = ${e.message}")
            null
        }
    }

    fun decryptPin(cipherText: String): ByteArray? {
        val array = cipherText.split(DIVIDER)
        val cipherData = Base64.decode(array[0], Base64.DEFAULT)
        val iv = Base64.decode(array[1], Base64.DEFAULT)

        return try {
            val cipher = getDecryptCipherForIv(iv)
            val clearText = cipher.doFinal(cipherData)

            clearText
        } catch (e: Exception) {
            Log.e(TAG, "decrypt: error msg = ${e.message}")
            null
        }
    }

    fun decrypt(cipherText: String): String? {
        val array = cipherText.split(DIVIDER)
        val cipherData = Base64.decode(array[0], Base64.DEFAULT)
        val iv = Base64.decode(array[1], Base64.DEFAULT)

        return try {
            val cipher = getDecryptCipherForIv(iv)
            val clearText = cipher.doFinal(cipherData)

            String(clearText, 0, clearText.size, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            Log.e(TAG, "decrypt: error msg = ${e.message}")
            null
        }
    }
}