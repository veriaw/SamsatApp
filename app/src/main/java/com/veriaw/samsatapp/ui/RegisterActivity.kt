package com.veriaw.samsatapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.veriaw.kriptografiapp.data.entity.UserEntity
import com.veriaw.kriptografiapp.model.UserViewModel
import com.veriaw.kriptografiapp.model.ViewModelFactory
import com.veriaw.samsatapp.databinding.ActivityRegisterBinding
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import kotlin.text.Charsets.UTF_8

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = obtainViewModel(this)

        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            val email = binding.etEmail.text.toString()
            // Check if the key already exists
            val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val keyExists = sharedPreferences.contains("secret_key")

            val key: SecretKey = if (!keyExists) {
                // Generate a new AES key if it doesn't exist
                generateAESKey()
            } else {
                // Retrieve the existing key and IV
                val pair = getSecretKeyAndIV(this)
                pair.first ?: generateAESKey() // Fallback to generating a new key if retrieval fails
            }

            val pair = encryptAES(password, key)

            // Save the IV if it's a new key
            if (!keyExists) {
                saveSecretKeyAndIV(key, pair.second, this)
            }

            val user = UserEntity().apply {
                this.username = username
                this.email = email
                this.password = Base64.encodeToString(pair.first, Base64.DEFAULT)
                this.role = "User "
            }

            viewModel.registerUser (user)
            showToast("Berhasil Mendaftarkan akun!")
        }

        binding.btnLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    fun generateAESKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256) // 128, 192, atau 256 bit
        return keyGen.generateKey()
    }

    // Mengenkripsi teks menggunakan AES
    fun encryptAES(data: String, secretKey: SecretKey): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv // Inisialisasi vector (IV) untuk dekripsi
        val encryptedData = cipher.doFinal(data.toByteArray(UTF_8))
        return Pair(encryptedData, iv)
    }

    fun saveSecretKeyAndIV(secretKey: SecretKey, iv: ByteArray, context: Context) {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val encodedKey = Base64.encodeToString(secretKey.encoded, Base64.DEFAULT)
        val ivString = iv.joinToString(",") { it.toString() }

        sharedPreferences.edit()
            .putString("secret_key", encodedKey)
            .putString("iv", ivString)
            .apply()
    }

    fun getSecretKeyAndIV(context: Context): Pair<SecretKey?, ByteArray?> {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        val encodedKey = sharedPreferences.getString("secret_key", null)
        val ivString = sharedPreferences.getString("iv", null)

        if (encodedKey != null && ivString != null) {
            // Decode key dari Base64 menjadi byte array
            val keyBytes = Base64.decode(encodedKey, Base64.DEFAULT)

            // Convert IV string menjadi byte array
            val iv = ivString.split(",").map { it.toByte() }.toByteArray()

            // Membuat SecretKeySpec dari byte array
            val keySpec = SecretKeySpec(keyBytes, "AES")

            return keySpec to iv
        }
        return null to null
    }

    private fun obtainViewModel(activity: AppCompatActivity): UserViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(UserViewModel::class.java)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}