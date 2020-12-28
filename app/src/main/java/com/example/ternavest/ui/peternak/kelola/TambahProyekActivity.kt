package com.example.ternavest.ui.peternak.kelola

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.example.ternavest.R
import com.example.ternavest.model.Location
import com.example.ternavest.viewmodel.LocationViewModel
import com.example.ternavest.viewmodel.ProyekViewModel
import kotlinx.android.synthetic.main.activity_tambah_proyek.*
import java.text.SimpleDateFormat
import java.util.*


class TambahProyekActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName


    private var fromDatePickerDialog: DatePickerDialog? = null
    private var toDatePickerDialog: DatePickerDialog? = null

    private var dateFormatter: SimpleDateFormat? = null

    private lateinit var proyekViewModel: ProyekViewModel
    private lateinit var lvm: LocationViewModel
    private lateinit var listProvinces: ArrayList<Location>
    private lateinit var listRegencies: ArrayList<Location>
    private lateinit var listDistricts : ArrayList<Location>
    private var idProvince = 0
    private var idRegency = 0
    private var idDistrict = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_proyek)

        setSupportActionBar(toolbartambahpproyek)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        proyekViewModel = ViewModelProvider(this, NewInstanceFactory()).get(ProyekViewModel::class.java)

        val namaProyek = txtNamaProyek.text.toString()
        val deskripsiProyek = txtDeskripsiProyek.text.toString()
        val jenisHewan = txtJenisHewan.text.toString()
        val roi = txtRoi.text.toString()
//        val roiInt = roi.toInt()
        val waktuMulai = txtWaktuMulai.text.toString()
        val waktuSelesai = txtWaktuSelesai.text.toString()

        dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        txtWaktuMulai.setInputType(InputType.TYPE_NULL)
        txtWaktuMulai.requestFocus()

        txtWaktuSelesai.setInputType(InputType.TYPE_NULL)
        setDateTimeField()


    }

    private fun setDateTimeField() {

        val newCalendar: Calendar = Calendar.getInstance()
        fromDatePickerDialog = DatePickerDialog(this, OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val newDate: Calendar = Calendar.getInstance()
            newDate.set(year, monthOfYear, dayOfMonth)
            txtWaktuMulai.setText(dateFormatter!!.format(newDate.getTime()))
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH))

        toDatePickerDialog = DatePickerDialog(this, OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val newDate: Calendar = Calendar.getInstance()
            newDate.set(year, monthOfYear, dayOfMonth)
            txtWaktuSelesai.setText(dateFormatter!!.format(newDate.getTime()))
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH))

        txtWaktuMulai.setOnClickListener{ fromDatePickerDialog?.show() }
        txtWaktuSelesai.setOnClickListener { toDatePickerDialog?.show() }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}