package com.example.ternavest.ui.peternak.kelola.proyek

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.example.ternavest.R
import com.example.ternavest.model.Location
import com.example.ternavest.model.Proyek
import com.example.ternavest.ui.peternak.main.PeternakActivity
import com.example.ternavest.viewmodel.LocationViewModel
import com.example.ternavest.viewmodel.ProyekViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_tambah_proyek.*
import kotlinx.android.synthetic.main.layout_add_update_proyek.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditProyekActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    private val TAG = javaClass.simpleName

    private var fromDatePickerDialog: DatePickerDialog? = null
    private var toDatePickerDialog: DatePickerDialog? = null
    private val PICK_IMAGE_REQUEST = 22
    private var dateFormatter: SimpleDateFormat? = null
    var objectStorageReference: StorageReference? = null
    var objectFirebaseFirestore: FirebaseFirestore? = null
    private var firebaseUser: FirebaseUser? = null
    private val listProyekId: MutableList<Int> = ArrayList()

    private lateinit var proyekViewModel: ProyekViewModel
    private lateinit var lvm: LocationViewModel
    private  var listProvinces: ArrayList<Location> = ArrayList()
    private  var listRegencies: ArrayList<Location> = ArrayList()
    private  var listDistricts : ArrayList<Location> = ArrayList()
    private var idProvince = 0
    private var idRegency = 0
    private var idDistrict = 0
    private var filePath: Uri? = null
    private lateinit var p : Proyek

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_proyek)

        p = intent.getParcelableExtra<Proyek>("proyek")!!

        setSupportActionBar(toolbartambahpproyek)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbartambahpproyek.setOnMenuItemClickListener {item ->
                when (item.getItemId()) {
                    R.id.action_delete -> {
                        proyekViewModel.delete(p.id!!)
                        startActivity(Intent(this, PeternakActivity::class.java))
                        true

                    }
                    else -> super.onOptionsItemSelected(item)
                }
        }

        proyekViewModel = ViewModelProvider(this, NewInstanceFactory()).get(ProyekViewModel::class.java)
        lvm = ViewModelProvider(this, NewInstanceFactory()).get(LocationViewModel::class.java)
        objectStorageReference = FirebaseStorage.getInstance().getReference("imageFolder")
        objectFirebaseFirestore = FirebaseFirestore.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser



        dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        txtWaktuMulai.setInputType(InputType.TYPE_NULL)
        txtWaktuMulai.requestFocus()
        txtWaktuSelesai.setInputType(InputType.TYPE_NULL)
        setDateTimeField()
        initWilayah()
        loadProvinces()
        setEditText(p)

        Picasso.get()
                .load(p?.photoProyek)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.load_image)
                .into(imgUpload)

        btnUploadImage.setOnClickListener { selectImage() }

        btnSimpan.setOnClickListener(View.OnClickListener { v: View? ->
            val photo = p.photoProyek
            val namaProyek = txtNamaProyek.text.toString()
            val deskripsiProyek = txtDeskripsiProyek.text.toString()
            val jenisHewan = txtJenisHewan.text.toString()
            val roi = txtRoi.text.toString()
//            val roiInt = roi.toInt()
            val waktuMulai = txtWaktuMulai.text.toString()
            val waktuSelesai = txtWaktuSelesai.text.toString()
            val alamat: String = txtAlamatLengkap.getText().toString()
            var kecamatan: String? = null
            kecamatan = if (spin_districts != null && spin_districts.getSelectedItem() != null) {
                spin_districts.getSelectedItem() as String
            } else {
                "-"
            }
            var kabupaten: String? = null
            kabupaten = if (spin_regencies != null && spin_regencies.getSelectedItem() != null) {
                spin_regencies.getSelectedItem() as String
            } else {
                "-"
            }

            var prov: String? = null
            prov = if (spin_provinces != null && spin_provinces.getSelectedItem() != null) {
                spin_provinces.getSelectedItem() as String
            } else {
                "-"
            }

            var cek = true
            if (namaProyek.length <= 0) {
                txtNamaProyek.setError("Masukkan nama proyek")
                cek = false
            }

            if (TextUtils.isEmpty(deskripsiProyek)) {
                txtDeskripsiProyek.setError("Masukkan deskripsi proyek")
                cek = false
            }

            if (TextUtils.isEmpty(jenisHewan)) {
                txtJenisHewan.setError("Masukkan jenis hewan")
                cek = false
            }

            if (TextUtils.isEmpty(roi)) {
                txtRoi.setError("Masukkan ROI")
                cek = false
            }

            if (TextUtils.isEmpty(waktuMulai)) {
                txtWaktuMulai.setError("Masukkan tanggal mulai")
                cek = false
            }

            if (TextUtils.isEmpty(waktuSelesai)) {
                txtWaktuSelesai.setError("Masukkan tanggal selesai")
                cek = false
            }

            val biayaHewan = txtBiayaPengelolaan.text.toString()
            val biayaPengelolahana = biayaHewan.toLong()

            if (TextUtils.isEmpty(biayaHewan)) {
                txtBiayaPengelolaan.setError("Masukkan biaya perhewan")
                cek = false
            }

            if (TextUtils.isEmpty(alamat)) {
                txtAlamatLengkap.setError("Masukkan alamat lengkap proyek")
                cek = false
            }


            if (TextUtils.isEmpty(photo)) {
                Toast.makeText(this, "Upload foto dulu", Toast.LENGTH_SHORT).show()
                cek = false
            }

            val roii = txtRoi.text.toString()
            val roiInt = roii.toInt()

            if (TextUtils.isEmpty(roi)) {
                txtRoi.setError("Masukkan ROI")
                cek = false
            }


            if (TextUtils.isEmpty(biayaHewan)) {
                txtBiayaPengelolaan.setError("Masukkan biaya perhewan")
                cek = false
            }

            val p = Proyek(p.id,firebaseUser?.uid, namaProyek, deskripsiProyek, jenisHewan, roiInt, waktuMulai, waktuSelesai,
                    biayaPengelolahana,prov,kabupaten, kecamatan,alamat,photo,p.peminat)

            if (cek) {
                proyekViewModel.update(p)
                Toast.makeText(this, "Update Berhasil", Toast.LENGTH_SHORT).show()
                finish()
            }

        })

    }

    private fun setEditText(p : Proyek?) {

        txtNamaProyek.setText(p?.namaProyek)
        txtDeskripsiProyek.setText(p?.deskripsiProyek)
        txtJenisHewan.setText(p?.jenisHewan)
        txtRoi.setText(p?.roi.toString())
        txtWaktuMulai.setText(p?.waktuMulai)
        txtWaktuSelesai.setText(p?.waktuSelesai)
        txtBiayaPengelolaan.setText(p?.biayaHewan.toString())
        txtAlamatLengkap.setText(p?.alamatLengkap)

    }


    private fun initWilayah() {
        cb_provinces.setEnabled(true)
        cb_regencies.setEnabled(false)
        cb_districts.setEnabled(false)
        spin_provinces.setEnabled(false)
        spin_regencies.setEnabled(false)
        spin_districts.setEnabled(false)

        cb_provinces.setOnCheckedChangeListener(this)
        cb_regencies.setOnCheckedChangeListener(this)
        cb_districts.setOnCheckedChangeListener(this)
        spin_provinces.setOnItemSelectedListener(this)
        spin_regencies.setOnItemSelectedListener(this)
        spin_districts.setOnItemSelectedListener(this)
        cb_provinces.isEnabled = true
        cb_regencies.isEnabled = false
        cb_districts.isEnabled = false
        spin_provinces.setEnabled(false)
        spin_regencies.setEnabled(false)
        spin_districts.setEnabled(false)
        cb_provinces.setOnCheckedChangeListener(this)
        cb_regencies.setOnCheckedChangeListener(this)
        cb_districts.setOnCheckedChangeListener(this)
        spin_provinces.setOnItemSelectedListener(this)
        spin_regencies.setOnItemSelectedListener(this)
        spin_districts.setOnItemSelectedListener(this)
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

    private fun loadProvinces() {
        lvm.loadProvinces()
        lvm.provinces.observe(this, Observer { provinces ->
            if (provinces != null) {
                listProvinces = java.util.ArrayList()
                val itemList: MutableList<String> = java.util.ArrayList()
                for (attributes in provinces.provinces) { // Fix nama provinsi
                    if (attributes.id == 31) listProvinces.add(Location(attributes.id, "DKI Jakarta")) else if (attributes.id == 34) listProvinces.add(Location(attributes.id, "DI Yogyakarta")) else listProvinces.add(Location(attributes.id, attributes.name))
                }
                for (location in listProvinces) itemList.add(location.name)
                val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, itemList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spin_provinces.setAdapter(adapter)
                if (p?.provinsi != "-"){
                    spin_provinces.setSelection(adapter.getPosition(p.provinsi))
                }
            }
        })
    }

    private fun loadRegencies(idProvince: Int) {
        lvm.loadRegencies(idProvince)
        lvm.regencies.observe(this, Observer { regencies ->
            if (regencies != null) {
                listRegencies = java.util.ArrayList()
                val itemList: MutableList<String> = java.util.ArrayList()
                for (attributes in regencies.regencies) listRegencies.add(Location(attributes.id, attributes.name))
                for (location in listRegencies) itemList.add(location.name)
                val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, itemList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spin_regencies.setAdapter(adapter)
                if (p.kabupaten != "-"){
                    spin_regencies.setSelection(adapter.getPosition(p.kabupaten))
                }
            }
        })
    }

    private fun loadDistricts(idRegency: Int) {
        lvm.loadDistricts(idRegency)
        lvm.districts.observe(this, Observer { districts ->
            if (districts != null) {
                listDistricts = java.util.ArrayList()
                val itemList: MutableList<String> = java.util.ArrayList()
                for (attributes in districts.districts) listDistricts.add(Location(attributes.id, attributes.name))
                for (location in listDistricts) itemList.add(location.name)
                val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, itemList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spin_districts.setAdapter(adapter)
                if (p.kecamatan != "-"){
                    spin_districts.setSelection(adapter.getPosition(p.kecamatan))
                }
            }
        })
    }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
        when (adapterView.id) {
            R.id.spin_provinces -> {
                idProvince = listProvinces[i].id
                loadRegencies(idProvince)
            }
            R.id.spin_regencies -> {
                idRegency = listRegencies[i].id
                loadDistricts(idRegency)
            }
            R.id.spin_districts -> idDistrict = listDistricts[i].id
        }
    }

    override fun onNothingSelected(adapterView: AdapterView<*>?) {}

    override fun onCheckedChanged(compoundButton: CompoundButton, b: Boolean) {
        when (compoundButton.id) {
            R.id.cb_provinces -> {
                spin_provinces.setEnabled(b)
                cb_regencies.isEnabled = b
                if (!b) {
                    cb_regencies.isChecked = false
                    cb_districts.isChecked = false
                }
            }
            R.id.cb_regencies -> {
                spin_regencies.isEnabled = b
                cb_districts.isEnabled = b
                if (!b) cb_districts.isChecked = false
            }
            R.id.cb_districts -> spin_districts.isEnabled = b
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

    private fun uploadImage() {
        if (filePath != null) {
            btnUploadImage.setText("Mengupload..")
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
                    btnUploadImage.visibility = View.INVISIBLE
                    Toast.makeText(this, "Upload Gambar Berhasil", Toast.LENGTH_SHORT).show()
                    btnSimpan.setOnClickListener(View.OnClickListener { v: View? ->
                        val photo = Objects.requireNonNull(task.result).toString()
                        val namaProyek = txtNamaProyek.text.toString()
                        val deskripsiProyek = txtDeskripsiProyek.text.toString()
                        val jenisHewan = txtJenisHewan.text.toString()
                        val roi = txtRoi.text.toString()
                        val roiInt = roi.toInt()
                        val waktuMulai = txtWaktuMulai.text.toString()
                        val waktuSelesai = txtWaktuSelesai.text.toString()
                        val biayaHewan = txtBiayaPengelolaan.text.toString()
                        val biayaPengelolahan = biayaHewan.toLong()
                        val alamat: String = txtAlamatLengkap.getText().toString()
                        var kecamatan: String? = null
                        kecamatan = if (spin_districts != null && spin_districts.getSelectedItem() != null) {
                            spin_districts.getSelectedItem() as String } else { "-" }
                        var kabupaten: String? = null
                        kabupaten = if (spin_regencies != null && spin_regencies.getSelectedItem() != null) {
                            spin_regencies.getSelectedItem() as String
                        } else { "-" }
                        var prov: String? = null
                        prov = if (spin_provinces != null && spin_provinces.getSelectedItem() != null) {
                            spin_provinces.getSelectedItem() as String } else { "-" }
                        var cek = true
                        if (namaProyek.length <= 0) {
                            txtNamaProyek.setError("Masukkan nama proyek")
                            cek = false
                        }
                        if (TextUtils.isEmpty(deskripsiProyek)) {
                            txtDeskripsiProyek.setError("Masukkan deskripsi proyek")
                            cek = false
                        }
                        if (TextUtils.isEmpty(jenisHewan)) {
                            txtJenisHewan.setError("Masukkan jenis hewan")
                            cek = false
                        }
                        if (TextUtils.isEmpty(roi)) {
                            txtRoi.setError("Masukkan ROI")
                            cek = false
                        }
                        if (TextUtils.isEmpty(waktuMulai)) {
                            txtWaktuMulai.setError("Masukkan tanggal mulai")
                            cek = false
                        }
                        if (TextUtils.isEmpty(waktuSelesai)) {
                            txtWaktuSelesai.setError("Masukkan tanggal selesai")
                            cek = false
                        }
                        if (TextUtils.isEmpty(biayaHewan)) {
                            txtBiayaPengelolaan.setError("Masukkan biaya perhewan")
                            cek = false
                        }
                        if (TextUtils.isEmpty(alamat)) {
                            txtAlamatLengkap.setError("Masukkan alamat lengkap proyek")
                            cek = false
                        }
                        if (TextUtils.isEmpty(p.photoProyek)) {
                            Toast.makeText(this, "Upload foto dulu", Toast.LENGTH_SHORT).show()
                            cek = false
                        }
                        val p = Proyek(p.id,firebaseUser?.uid, namaProyek, deskripsiProyek, jenisHewan, roiInt, waktuMulai, waktuSelesai,
                                biayaPengelolahan,prov,kabupaten, kecamatan,alamat,photo,p.peminat)

                        if (cek) {
                            proyekViewModel.update(p)
                            Toast.makeText(this, "Update Berhasil", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    })
                } else if (!task.isSuccessful) {
                    Toast.makeText(this, Objects.requireNonNull(task.exception).toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
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
                imgUpload.setImageBitmap(bitmap)
                btnUploadImage.setText("Upload")
                btnUploadImage.setOnClickListener{ uploadImage() }
            } catch (e: IOException) {
                // Log the exception
                e.printStackTrace()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_delete, menu)
        return true
    }



}