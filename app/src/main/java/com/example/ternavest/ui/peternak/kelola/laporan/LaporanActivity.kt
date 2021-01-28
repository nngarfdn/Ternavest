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
import com.example.ternavest.adapter.recycler.LaporanAdapter
import com.example.ternavest.model.Laporan
import com.example.ternavest.model.Proyek
import com.example.ternavest.utils.AppUtils.LEVEL_INVESTOR
import com.example.ternavest.viewmodel.LaporanViewModel
import com.example.ternavest.viewmodel.ProyekViewModel
import kotlinx.android.synthetic.main.activity_laporan.*

class LaporanActivity : AppCompatActivity() {


    companion object{
        private const val TAG = "LaporanActivity"
    }
    private lateinit var laporanViewModel: LaporanViewModel
    private lateinit var proyekViewModel: ProyekViewModel
    private var p: Proyek? = null
    private var id: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        p = intent?.getParcelableExtra("proyek")
        id = intent?.getStringExtra("id")
        val level : String? = intent?.getStringExtra("level")

        if (level?.equals(LEVEL_INVESTOR)!!){
            floatingActionButton2.visibility = View.INVISIBLE
        } else {
            floatingActionButton2.visibility = View.VISIBLE
        }

        imgLaporanKosong.visibility = View.INVISIBLE
        txtProyekKosong.visibility = View.INVISIBLE
        shimmerKelola.startShimmerAnimation()

        laporanViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(LaporanViewModel::class.java)
        proyekViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ProyekViewModel::class.java)

        proyekViewModel.getResultByID().observe(this, Observer<List<Proyek>> { result ->
            Log.d(TAG, "onCreate: $result")
            result.forEach { p ->
                run {
                    laporanViewModel.loadResultByProyekID(p.id!!)
                    laporanViewModel.getResultByProyekID().observe(this, Observer<List<Laporan>> { result ->
                        Log.d(TAG, "onCreate: $result")
                        if (result.isNotEmpty()){
                            imgLaporanKosong.visibility = View.INVISIBLE
                            txtProyekKosong.visibility = View.INVISIBLE
                            shimmerKelola.visibility = View.INVISIBLE
                            shimmerKelola.stopShimmerAnimation()
                            val layoutManager = LinearLayoutManager(this)
                            rv_laporan.layoutManager = layoutManager
                            val adapter = LaporanAdapter(result)
                            rv_laporan.adapter = adapter
                        } else {
                            imgLaporanKosong.visibility = View.VISIBLE
                            txtProyekKosong.visibility = View.VISIBLE
                            shimmerKelola.visibility = View.INVISIBLE
                            shimmerKelola.stopShimmerAnimation()
                            val layoutManager = LinearLayoutManager(this)
                            rv_laporan.layoutManager = layoutManager
                            val adapter = LaporanAdapter(result)
                            rv_laporan.adapter = adapter

                        }


                    })
                }
            }

        })

        floatingActionButton2.setOnClickListener {
            val intent = Intent(this, TambahLaporanActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)

            laporanViewModel.getResultByProyekID().observe(this, Observer<List<Laporan>> { result ->
                Log.d(TAG, "onCreate: $result")
                imgLaporanKosong.visibility = View.INVISIBLE
                val layoutManager = LinearLayoutManager(this)
                rv_laporan.layoutManager = layoutManager
                val adapter = LaporanAdapter(result)
                rv_laporan.adapter = adapter
            })

        }
    }

    override fun onStart() {
        p?.id?.let { laporanViewModel.loadResultByProyekID(it) }
        id?.let { proyekViewModel.loadResultByID(it) }
        super.onStart()
    }

    override fun onResume() {
        p?.id?.let { laporanViewModel.loadResultByProyekID(it) }
        super.onResume()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}