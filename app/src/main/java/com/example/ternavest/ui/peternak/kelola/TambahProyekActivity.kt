package com.example.ternavest.ui.peternak.kelola

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.example.ternavest.R
import com.example.ternavest.viewmodel.ProyekViewModel
import kotlinx.android.synthetic.main.activity_tambah_proyek.*

class TambahProyekActivity : AppCompatActivity() {

    private var proyekViewModel: ProyekViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_proyek)

        setSupportActionBar(toolbartambahpproyek)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        proyekViewModel = ViewModelProvider(this, NewInstanceFactory()).get(ProyekViewModel::class.java)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}