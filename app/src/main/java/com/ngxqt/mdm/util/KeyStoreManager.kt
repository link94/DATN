package com.ngxqt.mdm.util

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import android.widget.Toast
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import javax.crypto.Cipher

class KeyStoreManager(private val context: Context) {
    private lateinit var keyStore: KeyStore
    private lateinit var keyPair: KeyPair

    companion object {
        const val ALIAS_TOKEN = "alias_token"

        private const val TAG = "KeyStoreManager"
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_RSA
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_ECB
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }

    init {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
        }
    }

    fun createKey(alias: String) {
        try {
            if (!keyStore.containsAlias(alias)) {
                val keyPairGenerator =
                    KeyPairGenerator.getInstance(ALGORITHM, "AndroidKeyStore")
                val parameterSpec = KeyGenParameterSpec.Builder(
                    alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setEncryptionPaddings(PADDING)
                    .setDigests(KeyProperties.DIGEST_SHA1)
                    .build()
                keyPairGenerator.initialize(parameterSpec)
                keyPair = keyPairGenerator.genKeyPair()
            } else Toast.makeText(context, "Alias exist!!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
        }
    }

    fun getKeyInfo(alias: String): String {
        //val privateKey: PrivateKey = (keyStore.getEntry(alias, null) as KeyStore.PrivateKeyEntry).privateKey
        val cert = keyStore.getCertificate(alias)
        val publicKey = cert.publicKey

        val publicKeyBytes: ByteArray = Base64.encode(publicKey.encoded, Base64.DEFAULT)
        val pubKeyString = String(publicKeyBytes)

        //val privateKeyBytes: ByteArray = Base64.encode(privateKey.encoded, Base64.DEFAULT)
        //val priKeyString = String(privateKeyBytes)
        return pubKeyString
    }

    fun encryptString(clearText: String, alias: String): String {
        val publicKey = keyStore.getCertificate(alias).publicKey
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val cipherText = cipher.doFinal(clearText.toByteArray(Charsets.UTF_8))

        //textEncrypt.text = Base64.encodeToString(cipherText, Base64.DEFAULT)
        return Base64.encodeToString(cipherText, Base64.NO_WRAP)
    }

    fun decryptString(cipherText: String?, alias: String?): String? {
        Log.d("decryptString","cipherText = ${cipherText}, alias = ${alias}")
        if (cipherText.isNullOrEmpty() || alias.isNullOrEmpty()) return null
        val privateKeyEntry = keyStore.getEntry(alias, null) as KeyStore.PrivateKeyEntry
        val privateKey = privateKeyEntry.privateKey
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptText = cipher.doFinal(Base64.decode(cipherText, Base64.DEFAULT))

        //textDecrypt.text = String(decryptText)
        return String(decryptText)
    }

    fun getAliases(): String {
        var aliasesString = ""
        val aliases = keyStore.aliases()
        while (aliases.hasMoreElements()) {
            aliasesString += "${aliases.nextElement()}, "
        }
        //textAliases.text = aliasesString
        return aliasesString
    }

    fun deleteKey(alias: String) {
        keyStore.deleteEntry(alias)
    }
}