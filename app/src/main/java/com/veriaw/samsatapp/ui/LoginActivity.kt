package com.veriaw.samsatapp.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.veriaw.kriptografiapp.model.UserViewModel
import com.veriaw.kriptografiapp.model.ViewModelFactory
import com.veriaw.samsatapp.databinding.ActivityLoginBinding
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.text.Charsets.UTF_8

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel=obtainViewModel(this)

        binding.btnLogin.setOnClickListener{
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            var encryptedString: String? = null
            viewModel.getUserByUsername(username).observe(this, Observer { user ->
                user?.let{
                    encryptedString=user.password
                    if(encryptedString!=null){
                        val pair=getSecretKeyAndIV(this)
                        val encryptedByte = Base64.decode(encryptedString, Base64.DEFAULT)
                        val compared= decryptAES(encryptedByte,pair.first,pair.second)
                        if(password==compared){
                            val sharedPreferences: SharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                            val editor: SharedPreferences.Editor = sharedPreferences.edit()
                            editor.putInt("userId",user.id)
                            editor.putString("username",user.username)
                            editor.putString("email",user.email)
                            editor.putString("role",user.role)
                            editor.apply()
                            val intent = Intent(this, UserActivity::class.java)
                            startActivity(intent)
                        }else{
                            showToast("Username atau Password anda salah!")
                        }
                    }
                }
            })
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    // Mendekripsi teks terenkripsi menggunakan AES
    fun decryptAES(encryptedData: ByteArray, secretKey: SecretKey?, iv: ByteArray?): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
        val decryptedData = cipher.doFinal(encryptedData)
        return String(decryptedData, UTF_8)
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