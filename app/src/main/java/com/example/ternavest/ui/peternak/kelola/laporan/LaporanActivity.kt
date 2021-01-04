package com.example.ternavest.ui.peternak.kelola.laporan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.ternavest.R
import com.example.ternavest.model.Proyek
import kotlinx.android.synthetic.main.activity_laporan.*

class LaporanActivity : AppCompatActivity() {


    private  val TAG = "LaporanActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan)

        val p : Proyek = intent?.getParcelableExtra<Proyek>("proyek")!!

        floatingActionButton2.setOnClickListener {
            val intent = Intent(this, TambahLaporanActivity::class.java)
            intent.putExtra("proyek",p)
            startActivity(intent) }
    }
}