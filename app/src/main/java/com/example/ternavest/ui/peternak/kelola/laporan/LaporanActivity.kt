package com.example.ternavest.ui.peternak.kelola.laporan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ternavest.R
import com.example.ternavest.adaper.LaporanAdaper
import com.example.ternavest.adaper.ProyekAdaper
import com.example.ternavest.model.Laporan
import com.example.ternavest.model.Proyek
import com.example.ternavest.viewmodel.LaporanViewModel
import kotlinx.android.synthetic.main.activity_laporan.*

class LaporanActivity : AppCompatActivity() {


    private  val TAG = "LaporanActivity"
    private lateinit var laporanViewModel : LaporanViewModel
    private lateinit var p : Proyek

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan)

        p = intent?.getParcelableExtra<Proyek>("proyek")!!
        laporanViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(LaporanViewModel::class.java)

        laporanViewModel.getResultByProyekID().observe(this, Observer<List<Laporan>> { result ->
            Log.d(TAG, "onCreate: $result")
            imgLaporanKosong.visibility = View.INVISIBLE
            val layoutManager = LinearLayoutManager(this)
            rv_laporan.setLayoutManager(layoutManager)
            val adapter = LaporanAdaper(result)
            rv_laporan.setAdapter(adapter)
        })
        floatingActionButton2.setOnClickListener {
            val intent = Intent(this, TambahLaporanActivity::class.java)
            intent.putExtra("proyek",p)
            startActivity(intent) }
    }

    override fun onStart() {
        laporanViewModel.loadResultByProyekID(p.id!!)
        super.onStart()
    }

    override fun onResume() {
        laporanViewModel.loadResultByProyekID(p.id!!)
        super.onResume()
    }
}