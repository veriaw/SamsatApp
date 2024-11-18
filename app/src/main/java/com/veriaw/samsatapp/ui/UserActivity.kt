package com.veriaw.samsatapp.ui

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.veriaw.kriptografiapp.model.ViewModelFactory
import com.veriaw.samsatapp.R
import com.veriaw.samsatapp.adapter.SubmissionAdapter
import com.veriaw.samsatapp.databinding.ActivityUserBinding
import com.veriaw.samsatapp.model.SubmissionViewModel

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private lateinit var viewModel: SubmissionViewModel
    private val STORAGE_PERMISSION_CODE = 23
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = obtainViewModel(this)
        val recycleView =binding.rvPengajuan
        val adapter = SubmissionAdapter()
        recycleView.adapter=adapter
        recycleView.layoutManager =LinearLayoutManager(this)
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("username",null)
        val userId = sharedPreferences.getInt("userId",0)
        binding.tvGreet.text="Selamat Datang,\n"+"$username"
        userId?.let { viewModel.getSubmissionByUser(it).observe(this, Observer {submission->
            adapter.submitList(submission)
        }) }
        binding.btnPengajuan.setOnClickListener {
            val intent = Intent(this, AssignActivity::class.java)
            startActivity(intent)
        }
        binding.btnLogout.setOnClickListener {
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): SubmissionViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(SubmissionViewModel::class.java)
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1001)
        }
    }
    
}
