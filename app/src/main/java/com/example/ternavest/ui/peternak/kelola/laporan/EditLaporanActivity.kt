package com.example.ternavest.ui.peternak.kelola.laporan

import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ternavest.R
import com.example.ternavest.model.Laporan
import com.example.ternavest.viewmodel.LaporanViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_laporan.*
import kotlinx.android.synthetic.main.add_edit_laporan.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class EditLaporanActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    private var fromDatePickerDialog: DatePickerDialog? = null
    private var toDatePickerDialog: DatePickerDialog? = null
    private val PICK_IMAGE_REQUEST = 22
    private var dateFormatter: SimpleDateFormat? = null
    var objectStorageReference: StorageReference? = null
    var objectFirebaseFirestore: FirebaseFirestore? = null
    private var firebaseUser: FirebaseUser? = null
    private val listProyekId: MutableList<Int> = ArrayList()

    private lateinit var laporanViewModel: LaporanViewModel
    private var filePath: Uri? = null
    private lateinit var p : Laporan


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_laporan)

        p = intent.getParcelableExtra("laporan")!!

        laporanViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(LaporanViewModel::class.java)
        setSupportActionBar(toolbartambahLaporan)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        objectStorageReference = FirebaseStorage.getInstance().getReference("imageFolder")
        objectFirebaseFirestore = FirebaseFirestore.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser

        dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        txtTanggalLaporan.setInputType(InputType.TYPE_NULL)
        txtTanggalLaporan.requestFocus()
        setDateTimeField()

        txtJudulLaporan.setText(p?.judulLaporan)
        txtDeskripsiLaporan.setText(p?.deskripsiLaporan)
        txtTanggalLaporan.setText(p?.tanggal)
        txtJenisHewanDetail.setText(p?.pemasukan.toString())
        txtRoiDetail.setText(p?.pengeluaran.toString())

        Picasso.get()
                .load(p?.photoLaporan) // resizes the image to these dimensions (in pixel)
                .placeholder(R.drawable.load_image)
                .into(imgUploadLaporan)


        btnSimpanLaporan.setOnClickListener {
            val photo = p?.photoLaporan
            val judul = txtJudulLaporan.text.toString()
            val deskripsi = txtDeskripsiLaporan.text.toString()
            val tanggal = txtTanggalLaporan.text.toString()
            val pemasukan = txtJenisHewanDetail.text.toString()
            val pengeluaran = txtJenisHewanDetail.text.toString()

            val vJudul = validasiStringEditText(judul, "Masukan judul laporan", txtJudulLaporan)
            val vDeskripsi = validasiStringEditText(deskripsi, "Masukan deskripsi laporan", txtDeskripsiLaporan)
            val vTgl = validasiStringEditText(tanggal, "Masukan tanggal", txtTanggalLaporan)
            val vPemasukan = validasiStringEditText(pemasukan, "Masukan pemasukan", txtJenisHewanDetail)
            val vPengeluaran = validasiStringEditText(pengeluaran, "Masukan Pengeluaran", txtRoiDetail)

            if (photo.equals("")) {
                Toast.makeText(this, "Upload foto dulu", Toast.LENGTH_SHORT).show()
            }

            val laporan = Laporan(p.id, p.idProyek,judul,deskripsi,tanggal,pemasukan.toLong(), pengeluaran.toLong(),photo)
            if (vJudul && vDeskripsi && vPemasukan && vPengeluaran && vTgl) {
                laporanViewModel.update(laporan)
                Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show()
                val i = Intent(this, LaporanActivity::class.java)
                i.putExtra("level", "peternak")
                i.putExtra("id", p.idProyek)
                startActivity(i)
            }

        }

        btnUploadImageLaporan.setOnClickListener { selectImage() }

        toolbartambahLaporan.setOnMenuItemClickListener {item ->
            when (item.getItemId()) {
                R.id.action_delete -> {
                    AlertDialog.Builder(this)
                            .setTitle("Setel ulang kata sandi")
                            .setMessage("Apakah kamu yakin ingin menghapus ?")
                            .setNegativeButton("Tidak", null)
                            .setPositiveButton("Ya") { dialogInterface, i ->
                                val proyekId = p?.idProyek
                                laporanViewModel.delete(p.id!!)
                                val intent = Intent(this, LaporanActivity::class.java)
                                intent.putExtra("level", "peternak")
                                intent.putExtra("id", proyekId)
                                startActivity(intent)
                            }.create().show()

                    true

                }
                else -> super.onOptionsItemSelected(item)
            }
        }

    }

    private fun uploadImage() {
        if (filePath != null) {
            btnUploadImageLaporan.setText("Mengupload..")
            val namaImage = UUID.randomUUID().toString() // diganti id produk di firebase
            val nameOfimage = namaImage + "." + getExtension(filePath!!)
            val imageRef = objectStorageReference!!.child(nameOfimage)
            val objectUploadTask = imageRef.putFile(filePath!!)
            objectUploadTask.continueWithTask { task: Task<UploadTask.TaskSnapshot?> ->
                if (!task.isSuccessful) {
                    throw Objects.requireNonNull(task.exception)!!
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task: Task<Uri?> ->
                if (task.isSuccessful) {
                    btnUploadImageLaporan.visibility = View.INVISIBLE
                    Toast.makeText(this, "Upload Gambar Berhasil", Toast.LENGTH_SHORT).show()
                    btnSimpanLaporan.setOnClickListener(View.OnClickListener { v: View? ->
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

                        var vPhoto : Boolean = true
                        if (TextUtils.isEmpty(photo)) {
                            Toast.makeText(this, "Upload foto dulu", Toast.LENGTH_SHORT).show()
                            vPhoto = false
                        }


                        val laporan = Laporan(p.id, p.idProyek,judul,deskripsi,tanggal,pemasukanInt, pengeluaranInt,photo)
                        if (vJudul && vDeskripsi && vPemasukan && vPengeluaran && vTgl && vPhoto) {
                            laporanViewModel.update(laporan)
                            Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show()
                            val i = Intent(this, LaporanActivity::class.java)
                            i.putExtra("id",p.idProyek)
                            startActivity(i)
                        }
                    })
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            // Get the Uri of data
            filePath = data.data
            try {
                // Setting image on image view using Bitmap
                val bitmap = MediaStore.Images.Media
                        .getBitmap(
                                Objects.requireNonNull(this).getContentResolver(),
                                filePath)
                imgUploadLaporan.setImageBitmap(bitmap)
                btnUploadImageLaporan.setText("Upload")
                btnUploadImageLaporan.setOnClickListener{ uploadImage() }
            } catch (e: IOException) {
                // Log the exception
                e.printStackTrace()
            }
        }
    }
    private fun getExtension(uri: Uri): String? {
        try {
            val objectContentResolver: ContentResolver = Objects.requireNonNull(this).getContentResolver()
            val objectMimeTypeMap = MimeTypeMap.getSingleton()
            return objectMimeTypeMap.getExtensionFromMimeType(objectContentResolver.getType(uri))
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
        return null
    }


    fun validasiStringEditText(string: String, textError : String, edt : EditText) : Boolean {
        var cek: Boolean
        if (TextUtils.isEmpty(string)) {
            edt.setError(textError)
            cek = false
        } else cek = true

        return cek
    }

    private fun setDateTimeField() {
        val newCalendar: Calendar = Calendar.getInstance()
        fromDatePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val newDate: Calendar = Calendar.getInstance()
            newDate.set(year, monthOfYear, dayOfMonth)
            txtTanggalLaporan.setText(dateFormatter!!.format(newDate.getTime()))
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH))


        txtTanggalLaporan.setOnClickListener{ fromDatePickerDialog?.show() }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_delete, menu)
        return true
    }

}