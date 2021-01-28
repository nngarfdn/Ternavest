package com.example.ternavest.ui.peternak.kelola.proyek

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.*
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.example.ternavest.R
import com.example.ternavest.model.Location
import com.example.ternavest.model.Portfolio
import com.example.ternavest.model.Proyek
import com.example.ternavest.ui.both.main.MainActivity
import com.example.ternavest.utils.DateUtils.DATE_FORMAT
import com.example.ternavest.viewmodel.LocationViewModel
import com.example.ternavest.viewmodel.PortfolioViewModel
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

@Suppress("DEPRECATION")
class EditProyekActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    companion object{
        private const val PICK_IMAGE_REQUEST = 22
    }

    private var fromDatePickerDialog: DatePickerDialog? = null
    private var toDatePickerDialog: DatePickerDialog? = null
    private var menuDelete: MenuItem? = null

    private lateinit var portfolioViewModel: PortfolioViewModel

    private var dateFormatter: SimpleDateFormat? = null
    private var objectStorageReference: StorageReference? = null
    private var objectFirebaseFirestore: FirebaseFirestore? = null
    private var firebaseUser: FirebaseUser? = null

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

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_proyek)

        portfolioViewModel = ViewModelProvider(this, NewInstanceFactory()).get(PortfolioViewModel::class.java)

        p = intent.getParcelableExtra("proyek")!!

        portfolioViewModel.queryPeminatSemua(p.id)
        portfolioViewModel.data.observe(this, Observer<ArrayList<Portfolio>> { portfolioList ->
            menuDelete?.isVisible = portfolioList.isEmpty()
        })
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_delete -> {
                    AlertDialog.Builder(this)
                            .setTitle("Hapus Proyek")
                            .setMessage("Apakah kamu yakin ingin menghapus?")
                            .setNegativeButton("Tidak", null)
                            .setPositiveButton("Ya") { _, _ ->
                                proyekViewModel.delete(p.id!!)
                                startActivity(Intent(this, MainActivity::class.java))
                            }.create().show()

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



        dateFormatter = SimpleDateFormat(DATE_FORMAT, Locale.US)
        txtWaktuMulai.inputType = InputType.TYPE_NULL
        txtWaktuMulai.requestFocus()
        txtWaktuSelesai.inputType = InputType.TYPE_NULL
        setDateTimeField()
        initWilayah()
        loadProvinces()
        setEditText(p)

        // Batasin jumlah digit
        txtBiayaPengelolaan.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(10)))
        txtRoi.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(3)))
        txtRoi.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {}
            @SuppressLint("SetTextI18n")
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.length > 0){
                    if (charSequence.toString().toInt() > 100) txtRoi.setText("100")
                    else if (charSequence.toString().toInt() == 0) txtRoi.setText("1")
                }
            }
        })

        Picasso.get()
                .load(p.photoProyek)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.load_image)
                .into(imgUpload)

        btnUploadImage.setOnClickListener { selectImage() }

        btnSimpan.setOnClickListener {
            val photo = p.photoProyek
            val namaProyek = txtNamaProyek.text.toString()
            val deskripsiProyek = txtDeskripsiProyek.text.toString()
            val jenisHewan = txtJenisHewan.text.toString()
            val roi = txtRoi.text.toString()
//            val roiInt = roi.toInt()
            val waktuMulai = txtWaktuMulai.text.toString()
            val waktuSelesai = txtWaktuSelesai.text.toString()
            val alamat: String = txtAlamatLengkap.text.toString()
            val kecamatan: String? = if (spin_districts != null && spin_districts.selectedItem != null) {
                spin_districts.selectedItem as String
            } else {
                "-"
            }
            val kabupaten: String? = if (spin_regencies != null && spin_regencies.selectedItem != null) {
                spin_regencies.selectedItem as String
            } else {
                "-"
            }

            val prov: String? = if (spin_provinces != null && spin_provinces.selectedItem != null) {
                spin_provinces.selectedItem as String
            } else {
                "-"
            }

            var cek = true
            if (namaProyek.isEmpty()) {
                txtNamaProyek.error = "Masukkan nama proyek"
                cek = false
            }

            if (TextUtils.isEmpty(deskripsiProyek)) {
                txtDeskripsiProyek.error = "Masukkan deskripsi proyek"
                cek = false
            }

            if (TextUtils.isEmpty(jenisHewan)) {
                txtJenisHewan.error = "Masukkan jenis hewan"
                cek = false
            }

            if (TextUtils.isEmpty(roi)) {
                txtRoi.error = "Masukkan ROI"
                cek = false
            }

            if (TextUtils.isEmpty(waktuMulai)) {
                txtWaktuMulai.error = "Masukkan tanggal mulai"
                cek = false
            }

            if (TextUtils.isEmpty(waktuSelesai)) {
                txtWaktuSelesai.error = "Masukkan tanggal selesai"
                cek = false
            }

            val biayaHewan = txtBiayaPengelolaan.text.toString()
            val biayaPengelolahana = biayaHewan.toLong()

            if (TextUtils.isEmpty(biayaHewan)) {
                txtBiayaPengelolaan.error = "Masukkan biaya perhewan"
                cek = false
            }

            if (TextUtils.isEmpty(alamat)) {
                txtAlamatLengkap.error = "Masukkan alamat lengkap proyek"
                cek = false
            }


            if (TextUtils.isEmpty(photo)) {
                Toast.makeText(this, "Upload foto dulu", Toast.LENGTH_SHORT).show()
                cek = false
            }

            val roii = txtRoi.text.toString()
            val roiInt = roii.toInt()

            if (TextUtils.isEmpty(roi)) {
                txtRoi.error = "Masukkan ROI"
                cek = false
            }


            if (TextUtils.isEmpty(biayaHewan)) {
                txtBiayaPengelolaan.error = "Masukkan biaya perhewan"
                cek = false
            }

            val p = Proyek(p.id, firebaseUser?.uid, namaProyek, deskripsiProyek, jenisHewan, roiInt, waktuMulai, waktuSelesai,
                    biayaPengelolahana, prov, kabupaten, kecamatan, alamat, photo, p.peminat)

            if (cek) {
                proyekViewModel.update(p)
                Toast.makeText(this, "Update Berhasil", Toast.LENGTH_SHORT).show()
                finish()
            }

        }

        txtDeskripsiProyek.setOnTouchListener { v, event ->
            @Suppress("DEPRECATED_IDENTITY_EQUALS")
            v.parent.requestDisallowInterceptTouchEvent(!(event.action and MotionEvent.ACTION_MASK === MotionEvent.ACTION_UP))
            false }
    }

    private fun setEditText(p: Proyek?) {

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
        spin_provinces.onItemSelectedListener = this
        spin_regencies.onItemSelectedListener = this
        spin_districts.onItemSelectedListener = this
    }

    private fun setDateTimeField() {

        val newCalendar: Calendar = Calendar.getInstance()
        fromDatePickerDialog = DatePickerDialog(this, OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val newDate: Calendar = Calendar.getInstance()
            newDate.set(year, monthOfYear, dayOfMonth)
            txtWaktuMulai.setText(dateFormatter!!.format(newDate.time))
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH))

        toDatePickerDialog = DatePickerDialog(this, OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val newDate: Calendar = Calendar.getInstance()
            newDate.set(year, monthOfYear, dayOfMonth)
            txtWaktuSelesai.setText(dateFormatter!!.format(newDate.time))
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
                spin_provinces.adapter = adapter
                if (p.provinsi != "-") {
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
                spin_regencies.adapter = adapter
                if (p.kabupaten != "-") {
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
                spin_districts.adapter = adapter
                if (p.kecamatan != "-") {
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
            val objectContentResolver: ContentResolver = Objects.requireNonNull(this).contentResolver
            val objectMimeTypeMap = MimeTypeMap.getSingleton()
            return objectMimeTypeMap.getExtensionFromMimeType(objectContentResolver.getType(uri))
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
        return null
    }

    @SuppressLint("SetTextI18n")
    private fun uploadImage() {
        if (filePath != null) {
            btnUploadImage.text = "Mengupload..."
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
                    btnUploadImage.visibility = View.INVISIBLE
                    txtWarningUpload.visibility = View.INVISIBLE
                    Toast.makeText(this, "Upload Gambar Berhasil", Toast.LENGTH_SHORT).show()
                    btnSimpan.setOnClickListener {
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
                        val alamat: String = txtAlamatLengkap.text.toString()
                        val kecamatan: String? = if (spin_districts != null && spin_districts.selectedItem != null) {
                            spin_districts.selectedItem as String } else { "-" }
                        val kabupaten: String? = if (spin_regencies != null && spin_regencies.selectedItem != null) {
                            spin_regencies.selectedItem as String
                        } else { "-" }
                        val prov: String? = if (spin_provinces != null && spin_provinces.selectedItem != null) {
                            spin_provinces.selectedItem as String } else { "-" }
                        var cek = true
                        if (namaProyek.isEmpty()) {
                            txtNamaProyek.error = "Masukkan nama proyek"
                            cek = false
                        }
                        if (TextUtils.isEmpty(deskripsiProyek)) {
                            txtDeskripsiProyek.error = "Masukkan deskripsi proyek"
                            cek = false
                        }
                        if (TextUtils.isEmpty(jenisHewan)) {
                            txtJenisHewan.error = "Masukkan jenis hewan"
                            cek = false
                        }
                        if (TextUtils.isEmpty(roi)) {
                            txtRoi.error = "Masukkan ROI"
                            cek = false
                        }
                        if (TextUtils.isEmpty(waktuMulai)) {
                            txtWaktuMulai.error = "Masukkan tanggal mulai"
                            cek = false
                        }
                        if (TextUtils.isEmpty(waktuSelesai)) {
                            txtWaktuSelesai.error = "Masukkan tanggal selesai"
                            cek = false
                        }
                        if (TextUtils.isEmpty(biayaHewan)) {
                            txtBiayaPengelolaan.error = "Masukkan biaya perhewan"
                            cek = false
                        }
                        if (TextUtils.isEmpty(alamat)) {
                            txtAlamatLengkap.error = "Masukkan alamat lengkap proyek"
                            cek = false
                        }
                        if (TextUtils.isEmpty(p.photoProyek)) {
                            Toast.makeText(this, "Upload foto dulu", Toast.LENGTH_SHORT).show()
                            cek = false
                        }
                        val p = Proyek(p.id, firebaseUser?.uid, namaProyek, deskripsiProyek, jenisHewan, roiInt, waktuMulai, waktuSelesai,
                                biayaPengelolahan, prov, kabupaten, kecamatan, alamat, photo, p.peminat)

                        if (cek) {
                            proyekViewModel.update(p)
                            Toast.makeText(this, "Update Berhasil", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                } else if (!task.isSuccessful) {
                    Toast.makeText(this, Objects.requireNonNull(task.exception).toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
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
                imgUpload.setImageBitmap(bitmap)
                btnUploadImage.text = "Upload"
                txtWarningUpload.visibility = View.VISIBLE
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

        menuDelete = menu!!.findItem(R.id.action_delete)
        menuDelete?.isVisible = false

        return true
    }



}