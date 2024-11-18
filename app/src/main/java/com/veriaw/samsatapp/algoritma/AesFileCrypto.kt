package com.veriaw.kriptografiapp.algoritma

import java.io.File
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class AesFileCrypto {
    fun generateSecretKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(256) // Ukuran kunci, bisa 128, 192, atau 256 bit
        return keyGenerator.generateKey()
    }

    // Fungsi untuk mengenkripsi data
    fun encryptData(data: ByteArray, secretKey: SecretKey, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivParams = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams)
        return cipher.doFinal(data)
    }

    // Fungsi untuk mendekripsi data
    fun decryptData(encryptedData: ByteArray, secretKey: SecretKey, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivParams = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams)
        return cipher.doFinal(encryptedData)
    }

}