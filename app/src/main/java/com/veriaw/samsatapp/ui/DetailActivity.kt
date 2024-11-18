package com.veriaw.samsatapp.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.veriaw.kriptografiapp.algoritma.AesFileCrypto
import com.veriaw.kriptografiapp.algoritma.RsaAlgorithm
import com.veriaw.kriptografiapp.algoritma.VignereChiper
import com.veriaw.kriptografiapp.model.ViewModelFactory
import com.veriaw.samsatapp.R
import com.veriaw.samsatapp.data.entity.KeyEntity
import com.veriaw.samsatapp.databinding.ActivityDetailBinding
import com.veriaw.samsatapp.model.KeyViewModel
import com.veriaw.samsatapp.model.SubmissionViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModelSubmission: SubmissionViewModel
    private lateinit var viewModelKey: KeyViewModel
    private lateinit var rsaAlgorithm: RsaAlgorithm
    private lateinit var vignereChiper: VignereChiper
    private lateinit var aesFile: AesFileCrypto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("username",null)
        val userId = sharedPreferences.getInt("userId",0)
        val role = sharedPreferences.getString("role",null)
        if(role=="Admin"){
            binding.tvPembayaran.visibility=View.VISIBLE
            binding.ImagePayment.visibility=View.VISIBLE
            binding.dropdown.visibility=View.VISIBLE
            binding.tfCost.visibility=View.VISIBLE
            binding.btnCancel.visibility=View.VISIBLE
            binding.btnSubmit.visibility=View.VISIBLE
        }
        setContentView(binding.root)
        checkPermission()
        rsaAlgorithm = RsaAlgorithm()
        vignereChiper = VignereChiper()
        aesFile = AesFileCrypto()
        viewModelSubmission = obtainViewModelSubmission(this)
        viewModelKey = obtainViewModelKey(this)
        val subId = intent.getIntExtra("SubmissionId", 0)
        Log.d("SECRET KEY","${subId}")
        subId?.let { viewModelKey.getKeySubmission(it).observe(this, Observer { key ->
            Log.d("SECRET KEY","${key.key}")
            val secretKey=key.key?.split(",")?.toTypedArray() //secretkey=vignerekey,rsaprivatekey,vignerekey,aeskey,ivaes
            viewModelSubmission.getDetailSubmission(subId).observe(this, Observer { submission->
                //address, file, photo
                //text handle
                binding.tvTipePengajuan.text=submission.submissiontype
                binding.tvUsername.text=submission.username
                binding.tvEmail.text=submission.email
                binding.tvYear.text=submission.extendyear.toString()
                rsaAlgorithm.privateKey= secretKey!![1].toInt()
                rsaAlgorithm.n=secretKey[2].toInt()
                val cipherTextVignere = submission.address?.let { ciphertext -> rsaAlgorithm.decoder(ciphertext) }
                val plainText = cipherTextVignere?.let { ciphertext -> vignereChiper.decryption(ciphertext,secretKey[0]) }
                binding.tvAddress.text=plainText
                //photo handle
                val uriImage = Uri.parse(submission.photo)
                val contentResolver = applicationContext.contentResolver
                val inputStream = this.contentResolver.openInputStream(uriImage)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                Log.d("Uri Image","${submission.photo}")
                Glide.with(this)
                    .load(bitmap)
                    .into(binding.imageBerkas)
                //file handle
//                val uriFile = Uri.parse(submission.file)
//                val fileName = getFileNameFromUri(this,uriFile)
//                val originalName = fileName?.replace("Encrypted","")
//                binding.originalName.text=originalName
//                val aesKey="${secretKey[3]}"+","+"${secretKey[4]}"
//                val aesKeyConverted = stringToKeyAndIv(aesKey)
//                val data = getByteArrayFromUri(this,uriFile)
//                val decryptedData = data?.let { data -> aesFile.decryptData(data,aesKeyConverted.first,aesKeyConverted.second) }
//                binding.btnSave.setOnClickListener {
//                    originalName?.let { name ->
//                        decryptedData?.let { data ->
//                            saveByteArrayToExternalFile(this,
//                                name, data
//                            )
//                        }
//                    }
//                }
            })
        })
        }

    }

    fun saveByteArrayToExternalFile(context: Context, fileName: String, byteArray: ByteArray): Boolean {
        return try {
            // Buat direktori di penyimpanan eksternal
            val externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            if (!externalDir.exists()) {
                externalDir.mkdirs() // Buat direktori jika tidak ada
            }

            // Buat file di dalam direktori
            val file = File(externalDir, fileName)
            Log.d("LOKASI FILE","${file.absolutePath}")
            // Tulis ByteArray ke file
            FileOutputStream(file).use { fos ->
                fos.write(byteArray)
            }

            true // Berhasil
        } catch (e: IOException) {
            e.printStackTrace()
            false // Gagal
        }
    }

    fun getByteArrayFromUri(context: Context, uri: Uri): ByteArray? {
        var byteArray: ByteArray? = null

        // Buka InputStream dari Uri
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            // Gunakan ByteArrayOutputStream untuk menampung data
            val byteArrayOutputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024) // Buffer untuk membaca data
            var bytesRead: Int

            // Baca data ke dalam buffer dan tulis ke ByteArrayOutputStream
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead)
            }

            // Konversi ByteArrayOutputStream ke ByteArray
            byteArray = byteArrayOutputStream.toByteArray()
        }

        return byteArray
    }

    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null

        // Jika URI adalah content URI
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                // Cek apakah cursor memiliki data
                if (it.moveToFirst()) {
                    // Ambil index kolom DISPLAY_NAME
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    // Ambil nama file
                    fileName = it.getString(nameIndex)
                }
            }
        } else if (uri.scheme == "file") {
            // Jika URI adalah file URI, ambil nama dari path
            fileName = uri.lastPathSegment
        }

        return fileName
    }

    fun stringToKeyAndIv(keyAndIvString: String): Pair<SecretKey, ByteArray> {
        // Pisahkan string menjadi dua bagian
        val parts = keyAndIvString.split(",")
        if (parts.size != 2) {
            throw IllegalArgumentException("Input string must contain a key and an IV separated by a comma.")
        }

        // Decode kunci rahasia dari Base64
        val encodedKey = parts[0]
        val decodedKey = Base64.decode(encodedKey, Base64.DEFAULT)
        val secretKey = SecretKeySpec(decodedKey, "AES")

        // Decode IV dari Base64
        val ivString = parts[1]
        val iv = Base64.decode(ivString, Base64.DEFAULT)

        return Pair(secretKey, iv)
    }

    private fun obtainViewModelSubmission(activity: AppCompatActivity): SubmissionViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(SubmissionViewModel::class.java)
    }

    private fun obtainViewModelKey(activity: AppCompatActivity): KeyViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(KeyViewModel::class.java)
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1001)
        }
    }
}