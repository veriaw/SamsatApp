package com.veriaw.samsatapp.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.veriaw.kriptografiapp.algoritma.AesFileCrypto
import com.veriaw.kriptografiapp.algoritma.RsaAlgorithm
import com.veriaw.kriptografiapp.algoritma.VignereChiper
import com.veriaw.kriptografiapp.model.ViewModelFactory
import com.veriaw.samsatapp.R
import com.veriaw.samsatapp.data.entity.KeyEntity
import com.veriaw.samsatapp.data.entity.SubmissionEntity
import com.veriaw.samsatapp.databinding.ActivityAssignBinding
import com.veriaw.samsatapp.model.KeyViewModel
import com.veriaw.samsatapp.model.SubmissionViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.crypto.SecretKey

class AssignActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAssignBinding
    private lateinit var viewModelSubmission: SubmissionViewModel
    private lateinit var viewModelKey: KeyViewModel
    private lateinit var rsaAlgorithm: RsaAlgorithm
    private lateinit var vignereChiper: VignereChiper
    private lateinit var aesFile: AesFileCrypto
    private var secretKey: String? = null //secretkey=vignerekey,rsaprivatekey,vignerekey,aeskey,ivaes
    private var currentFileUri: String? = null
    private var currentPictureUri: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssignBinding.inflate(layoutInflater)
        val typeSubmission = arrayOf("Perpanjang SIM","Perpanjang STNK","Pembayaran Pajak")
        (binding.typeSub as? MaterialAutoCompleteTextView)?.setSimpleItems(typeSubmission)
        setContentView(binding.root)
        rsaAlgorithm = RsaAlgorithm()
        vignereChiper = VignereChiper()
        aesFile = AesFileCrypto()
        viewModelSubmission = obtainViewModelSubmission(this)
        viewModelKey = obtainViewModelKey(this)
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("username",null)
        val userId = sharedPreferences.getInt("userId",0)
        val email = sharedPreferences.getString("email",null)

        binding.chooseImage.setOnClickListener {
            startGallery()
        }

        binding.pilihFile.setOnClickListener{
            showFileChooser()
        }

        binding.btnCancel.setOnClickListener {
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
        }
        binding.btnSubmit.setOnClickListener {
            //SuperEnkripsi Address
            val plainText=binding.tfAddress.editText?.text.toString()
            val keyVignere = vignereChiper.generateRandomKey(plainText.length)
            rsaAlgorithm.setKeys()
            val superKey = "$keyVignere"+","+"${rsaAlgorithm.privateKey}"+","+"${rsaAlgorithm.n}"
            val cipherVignere = vignereChiper.encryption(plainText,keyVignere)
            val cipherRsa = rsaAlgorithm.encoder(cipherVignere)
            //create model submission
            val submission = SubmissionEntity()
            submission.submissiontype=binding.typeSub.text.toString()
            submission.timestamp=getCurrentDateTime()
            submission.username=username
            submission.userid=userId
            submission.email=email
            submission.address=cipherRsa
            submission.extendyear=binding.etYear.text.toString().toInt()
            submission.file=currentFileUri
            submission.photo=currentPictureUri
            submission.status="Sedang Diproses"
            viewModelSubmission.insertSubmission(submission)
//            create mode key
            secretKey=superKey
            val key = KeyEntity()
            key.key=secretKey
            viewModelSubmission.insertedKeyId.observe(this, Observer { subId->
                key.submissionid=subId
                Log.d("LAST ROW ID","$subId")
                viewModelKey.insertKey(key)
            })
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery =registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ){ uri: Uri? ->
        uri?.let {
            currentPictureUri=uri.toString()
            binding.imageView.setImageURI(uri)
        }
    }

    private fun showFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        try{
            startActivityForResult(Intent.createChooser(intent,"Select a File"),100)
        }catch (exception: Exception){
            Toast.makeText(this,"Please Install a file manager", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val fileName = getFileNameFromUri(this,uri)
                binding.originalName.text=fileName
                val fileNameNewExt = fileName+"Encrypted"
                val dataByteArray = getByteArrayFromUri(this,uri)
                val key = aesFile.generateSecretKey()
                val iv = ByteArray(16)
                val superKey = saveKeyToString(key,iv)
                val encryptData = dataByteArray?.let { it1 -> aesFile.encryptData(it1,key,iv) }
                val fileUri = encryptData?.let { data ->
                    saveByteArrayToExternalFile(this,fileNameNewExt,
                        data
                    )
                }
                currentFileUri=fileUri.toString()
                secretKey=secretKey+","+superKey
            }
        }
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

    fun saveKeyToString(secretKey: SecretKey, iv: ByteArray): String {
        val encodedKey = Base64.encodeToString(secretKey.encoded, Base64.DEFAULT)
        val ivString = Base64.encodeToString(iv, Base64.DEFAULT)
        val keyString = "$encodedKey"+","+"$ivString"
        return keyString
    }

    fun saveByteArrayToExternalFile(context: Context, fileName: String, byteArray: ByteArray): Uri? {
        return try {
            // Buat direktori di penyimpanan eksternal
            val externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            if (!externalDir.exists()) {
                externalDir.mkdirs() // Buat direktori jika tidak ada
            }

            // Buat file di dalam direktori
            val file = File(externalDir, fileName)
            Log.d("LOKASI FILE", "${file.absolutePath}")

            // Tulis ByteArray ke file
            FileOutputStream(file).use { fos ->
                fos.write(byteArray)
            }

            // Kembalikan Uri dari file yang disimpan
            Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
            null // Kembalikan null jika gagal
        }
    }

    private fun getCurrentDateTime(): String? {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatter.format(calendar.time)
    }

    private fun obtainViewModelSubmission(activity: AppCompatActivity): SubmissionViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(SubmissionViewModel::class.java)
    }

    private fun obtainViewModelKey(activity: AppCompatActivity): KeyViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(KeyViewModel::class.java)
    }
}