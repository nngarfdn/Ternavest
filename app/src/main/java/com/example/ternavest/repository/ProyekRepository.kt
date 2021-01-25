package com.example.ternavest.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ternavest.model.Profile
import com.example.ternavest.model.Proyek
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

private const val TAG = "ProyekRepository"
class ProyekRepository {

    private val database = FirebaseFirestore.getInstance()
    private var resultProyekByUUID: MutableLiveData<List<Proyek>> = MutableLiveData()
    private var resultProyekByID: MutableLiveData<List<Proyek>> = MutableLiveData()
    private var resultProyek: MutableLiveData<List<Proyek>> = MutableLiveData()
    private var resultPeminat: MutableLiveData<List<Profile>> = MutableLiveData()

    fun getResultsByUUID(): LiveData<List<Proyek>> = resultProyekByUUID
    fun getResultsByID(): LiveData<List<Proyek>> = resultProyekByID
    fun getResults(): LiveData<List<Proyek>> = resultProyek
    fun getResultsPeminat(): LiveData<List<Profile>> = resultPeminat

    fun insert(proyek: Proyek) {
        val ref = database.collection("proyek").document()
        proyek.id = ref.id
        ref.set(hashMapProfile(proyek))
                .addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) Log.d(TAG, "Document was added") else
                        Log.w(TAG, "Error adding document", task.exception) }
    }

    fun update(proyek: Proyek) {
        val idProduk = proyek.id
        val item = hashMapProfile(proyek)

        if (idProduk != null) {
            database.collection("proyek").document(idProduk)
                    .set(item)
                    .addOnSuccessListener {
                        Log.d(TAG, "Succes update")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error adding document", e)
                    }
        }
    }

    fun delete(idProduk: String) {
        database.collection("proyek").document(idProduk)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error deleting document", e)
                }
    }

    fun getProyekByID(id: String) {
        val produkData: MutableList<Proyek> = ArrayList()
        val db = FirebaseFirestore.getInstance()
        val savedProdukList = ArrayList<Proyek>()
        db.collection("proyek")
                .orderBy("namaProyek")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val kategoriDocument = document.data["id"] as String
                        if (kategoriDocument == id) {
                            val pp = document.toObject(Proyek::class.java)
                            pp.id = document.id
                            savedProdukList.add(pp)
                            produkData.add(pp)
                            Log.d(TAG, "getDataByUUID size : ${savedProdukList.size} getDataByUUID: $pp")
                        }
                    }
                    resultProyekByID.value = produkData
                    Log.d(TAG, "readProduk size final getDataByUUID : ${savedProdukList.size}")
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting documents.", exception)
                }
    }

    fun getProyekByUUID(uuid: String) {
        val produkData: MutableList<Proyek> = ArrayList()
        val db = FirebaseFirestore.getInstance()
        val savedProdukList = ArrayList<Proyek>()
        db.collection("proyek")
                .orderBy("namaProyek")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val kategoriDocument = document.data["uuid"] as String
                        if (kategoriDocument == uuid) {
                            val pp = document.toObject(Proyek::class.java)
                            pp.id = document.id
                            savedProdukList.add(pp)
                            produkData.add(pp)
                            Log.d(TAG, "getDataByUUID size : ${savedProdukList.size} getDataByUUID: $pp")
                        }
                    }
                    resultProyekByUUID.value = produkData
                    Log.d(TAG, "readProduk size final getDataByUUID : ${savedProdukList.size}")
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting documents.", exception)
                }
    }

    fun getProyek() {
        val produkData: MutableList<Proyek> = ArrayList()
        val db = FirebaseFirestore.getInstance()
        val savedProdukList = ArrayList<Proyek>()
        db.collection("proyek")
                .orderBy("namaProyek")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val pp = document.toObject(Proyek::class.java)
                        pp.id = document.id
                        savedProdukList.add(pp)
                        produkData.add(pp)
                        Log.d(TAG, "getData size : ${savedProdukList.size} getData: $pp")
                    }
                    resultProyek.value = produkData
                    Log.d(TAG, "readProduk size final getData : ${savedProdukList.size}")
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting documents.", exception)
                }
    }

    fun getPeminat(listPeminat : List<String>) {
        val iterator: Iterator<String> = listPeminat.iterator()
        Log.d(TAG, "getPeminat iterator: ${iterator.next()}")
        val produkData: MutableList<Profile> = ArrayList()
        val db = FirebaseFirestore.getInstance()
        ArrayList<Profile>()
        while (iterator.hasNext()) {
            db.collection("profil").document(iterator.next())
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val product: Profile? = task.result!!.toObject(Profile::class.java)
                            Log.d(TAG, "getPeminat: ${product?.accountName}")
                            if (product != null) produkData.add(product)
                            if (!iterator.hasNext()) resultPeminat.value = produkData // Pemuatan id terakhir
                        }
                    }
        }
    }

    private fun hashMapProfile(proyek: Proyek): Map<String, Any?> {
        val document: MutableMap<String, Any?> = HashMap()
        document["id"] = proyek.id
        document["uuid"] = proyek.uuid
        document["namaProyek"] = proyek.namaProyek
        document["deskripsiProyek"] = proyek.deskripsiProyek
        document["jenisHewan"] = proyek.jenisHewan
        document["waktuMulai"] = proyek.waktuMulai
        document["waktuSelesai"] = proyek.waktuSelesai
        document["biayaHewan"] = proyek.biayaHewan
        document["provinsi"] = proyek.provinsi
        document["kabupaten"] = proyek.kabupaten
        document["roi"] = proyek.roi
        document["kecamatan"] = proyek.kecamatan
        document["alamatLengkap"] = proyek.alamatLengkap
        document["photoProyek"] = proyek.photoProyek
        document["peminat"] = proyek.peminat
        return document
    }
}