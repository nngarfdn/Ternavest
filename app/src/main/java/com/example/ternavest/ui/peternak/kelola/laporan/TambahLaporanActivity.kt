package com.example.ternavest.ui.peternak.kelola.laporan

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.ternavest.R
import com.example.ternavest.model.Laporan
import com.example.ternavest.utils.DateUtils.DATE_FORMAT
import com.example.ternavest.viewmodel.LaporanViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_tambah_laporan.*
import kotlinx.android.synthetic.main.add_edit_laporan.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class TambahLaporanActivity : AppCompatActivity() {

    companion object{
        private const val PICK_IMAGE_REQUEST = 22
    }
    private var fromDatePickerDialog: DatePickerDialog? = null

    private var dateFormatter: SimpleDateFormat? = null
    private var objectStorageReference: StorageReference? = null
    private var objectFirebaseFirestore: FirebaseFirestore? = null
    private var firebaseUser: FirebaseUser? = null

    private lateinit var laporanViewModel: LaporanViewModel
    private var filePath: Uri? = null
    private var p : String? = null


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_laporan)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        p = intent.getStringExtra("id")

        laporanViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(LaporanViewModel::class.java)
        objectStorageReference = FirebaseStorage.getInstance().getReference("imageFolder")
        objectFirebaseFirestore = FirebaseFirestore.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser

        dateFormatter = SimpleDateFormat(DATE_FORMAT, Locale.US)
        txtTanggalLaporan.inputType = InputType.TYPE_NULL
        txtTanggalLaporan.requestFocus()
        setDateTimeField()

        txtJenisHewanDetail.setText("0")
        txtRoiDetail.setText("0")

        // Batasin jumlah digit
        txtJenisHewanDetail.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(10)))
        txtRoiDetail.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(10)))

        btnSimpanLaporan.setOnClickListener {
            val judul = txtJudulLaporan.text.toString()
            val deskripsi = txtDeskripsiLaporan.text.toString()
            val tanggal = txtTanggalLaporan.text.toString()
            val pemasukan = txtJenisHewanDetail.text.toString()
            val pengeluaran = txtRoiDetail.text.toString()


            validasiStringEditText(judul, "Masukan judul laporan", txtJudulLaporan)
            validasiStringEditText(deskripsi, "Masukan deskripsi laporan", txtDeskripsiLaporan)
            validasiStringEditText(tanggal, "Masukan tanggal", txtTanggalLaporan)
            validasiStringEditText(pemasukan, "Masukan pemasukan", txtJenisHewanDetail)
            validasiStringEditText(pengeluaran, "Masukan Pengeluaran", txtRoiDetail)
            Toast.makeText(this, "Upload foto dulu", Toast.LENGTH_SHORT).show()
        }

        btnUploadImageLaporan.setOnClickListener { selectImage() }

        txtDeskripsiLaporan.setOnTouchListener { v, event ->
            @Suppress("DEPRECATED_IDENTITY_EQUALS")
            v.parent.requestDisallowInterceptTouchEvent(!(event.action and MotionEvent.ACTION_MASK === MotionEvent.ACTION_UP))
            false }
    }

    @SuppressLint("SetTextI18n")
    private fun uploadImage() {
        if (filePath != null) {
            btnUploadImageLaporan.text = "Mengupload..."
            val namaImage = UUID.randomUUID().toString() // diganti id produk di firebase
            val nameOfimage = namaImage + "." + getExtension(filePath!!)
            val imageRef = objectStorageReference!!.child(nameOfimage)
            val objectUploadTask = imageRef.putFile(filePath!!)
            objectUploadTask.continueWithTask { task: Task<UploadTask.TaskSnapshot?> ->
                if (!task.isSuccessful) {
                    val requireNonNull = Objects.requireNonNull(task.exception)
                    throw requireNonNull
                            ?: throw NullPointerException("Expression 'Objects.requireNonNull(task.exception)' must not be null")
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task: Task<Uri?> ->
                if (task.isSuccessful) {
                    btnUploadImageLaporan.visibility = View.INVISIBLE
                    txtWarningUploadL.visibility = View.INVISIBLE
                    Toast.makeText(this, "Upload Gambar Berhasil", Toast.LENGTH_SHORT).show()
                    btnSimpanLaporan.setOnClickListener {
                        val photo = Objects.requireNonNull(task.result).toString()
                        val judul = txtJudulLaporan.text.toString()
                        val deskripsi = txtDeskripsiLaporan.text.toString()
                        val tanggal = txtTanggalLaporan.text.toString()
                        val pemasukan = txtJenisHewanDetail.text.toString()
                        val pemasukanInt = pemasukan.toLong()
                        val pengeluaran = txtRoiDetail.text.toString()
                        val pengeluaranInt = pengeluaran.toLong()
                        val vJudul = validasiStringEditText(judul, "Masukan judul laporan", txtJudulLaporan)
                        val vDeskripsi = validasiStringEditText(deskripsi, "Masukan deskripsi laporan", txtDeskripsiLaporan)
                        val vTgl = validasiStringEditText(tanggal, "Masukan tanggal", txtTanggalLaporan)
                        val vPemasukan = validasiStringEditText(pemasukan, "Masukan pemasukan", txtJenisHewanDetail)
                        val vPengeluaran = validasiStringEditText(pengeluaran, "Masukan Pengeluaran", txtRoiDetail)

                        var vPhoto = true
                        if (TextUtils.isEmpty(photo)) {
                            Toast.makeText(this, "Upload foto dulu", Toast.LENGTH_SHORT).show()
                            vPhoto = false
                        }


                        val laporan = Laporan("", p ,judul,deskripsi,tanggal,pemasukanInt, pengeluaranInt,photo)
                        if (vJudul && vDeskripsi && vPemasukan && vPengeluaran && vTgl && vPhoto) {
                            laporanViewModel.insert(laporan)
                            Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show()
                            onBackPressed()
                        }
                    }
                } else if (!task.isSuccessful) {
                    Toast.makeText(this, Objects.requireNonNull(task.exception).toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun selectImage() {
        // Defining Implicit Intent to mobile gallery
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            // Get the Uri of data
            filePath = data.data
            try {
                // Setting image on image view using Bitmap
                val bitmap = MediaStore.Images.Media
                        .getBitmap(
                                Objects.requireNonNull(this).contentResolver,
                                filePath)
                imgUploadLaporan.setImageBitmap(bitmap)
                btnUploadImageLaporan.text = "Upload"
                txtWarningUploadL.visibility = View.VISIBLE
                btnUploadImageLaporan.setOnClickListener{ uploadImage() }
            } catch (e: IOException) {
                // Log the exception
                e.printStackTrace()
            }
        }
    }
    private fun getExtension(uri: Uri): String? {
        try {
            val objectContentResolver: ContentResolver = Objects.requireNonNull(this).contentResolver
            val objectMimeTypeMap = MimeTypeMap.getSingleton()
            return objectMimeTypeMap.getExtensionFromMimeType(objectContentResolver.getType(uri))
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
        return null
    }


    private fun validasiStringEditText(string: String, textError : String, edt : EditText) : Boolean {

        return if (TextUtils.isEmpty(string)) {
            edt.error = textError
            false
        } else true
    }

    private fun setDateTimeField() {
        val newCalendar: Calendar = Calendar.getInstance()
        fromDatePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val newDate: Calendar = Calendar.getInstance()
            newDate.set(year, monthOfYear, dayOfMonth)
            txtTanggalLaporan.setText(dateFormatter!!.format(newDate.time))
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH))


        txtTanggalLaporan.setOnClickListener{ fromDatePickerDialog?.show() }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}