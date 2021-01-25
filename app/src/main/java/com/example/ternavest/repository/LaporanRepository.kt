package com.example.ternavest.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ternavest.model.Laporan
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*
private const val TAG = "LaporanRepository"
class LaporanRepository {

    private val database = FirebaseFirestore.getInstance()

    private var resultProyekByID: MutableLiveData<List<Laporan>> = MutableLiveData()
    private var resultProyekByProyekID: MutableLiveData<List<Laporan>> = MutableLiveData()

    fun getResultsByID(): LiveData<List<Laporan>> = resultProyekByID
    fun getResultsByProyekID(): LiveData<List<Laporan>> = resultProyekByProyekID

    fun getLaporanByProyekID(idPoyek: String) {
        val produkData: MutableList<Laporan> = ArrayList()
        val db = FirebaseFirestore.getInstance()
        val savedProdukList = ArrayList<Laporan>()
        db.collection("laporan")
                .orderBy("tanggal", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val kategoriDocument = document.data["idProyek"] as String
                        if (kategoriDocument == idPoyek) {
                            val pp = document.toObject(Laporan::class.java)
                            pp.id = document.id
                            savedProdukList.add(pp)
                            produkData.add(pp)
                            Log.d(TAG, "getDataByID size : ${savedProdukList.size} getDataByUUID: $pp")
                        }
                    }
                    resultProyekByProyekID.value = produkData
                    Log.d(TAG, "readProduk size final getDataByUUID : ${savedProdukList.size}")
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting documents.", exception)
                }
    }

    fun getProyekByID(id: String) {
        val produkData: MutableList<Laporan> = ArrayList()
        val db = FirebaseFirestore.getInstance()
        val savedProdukList = ArrayList<Laporan>()
        db.collection("laporan")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val kategoriDocument = document.data["id"] as String
                        if (kategoriDocument == id) {
                            val pp = document.toObject(Laporan::class.java)
                            pp.id = document.id
                            savedProdukList.add(pp)
                            produkData.add(pp)
                            Log.d(TAG, "getDataByID size : ${savedProdukList.size} getDataByUUID: $pp")
                        }
                    }
                    resultProyekByID.value = produkData
                    Log.d(TAG, "readProduk size final getDataByUUID : ${savedProdukList.size}")
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting documents.", exception)
                }
    }

    fun insert(laporan: Laporan) {
        val ref = database.collection("laporan").document()
        laporan.id = ref.id
        ref.set(hashMapProfile(laporan))
                .addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) Log.d(TAG, "Document was added") else
                        Log.w(TAG, "Error adding document", task.exception) }
    }

    fun update(lapporan: Laporan) {
        val idProduk = lapporan.id
        val item = hashMapProfile(lapporan)

        if (idProduk != null) {
            database.collection("laporan").document(idProduk)
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
        database.collection("laporan").document(idProduk)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error deleting document", e)
                }
    }

    private fun hashMapProfile(laporan: Laporan): Map<String, Any?> {
        val document: MutableMap<String, Any?> = HashMap()
        document["id"] = laporan.id
        document["idProyek"] = laporan.idProyek
        document["judulLaporan"] = laporan.judulLaporan
        document["deskripsiLaporan"] = laporan.deskripsiLaporan
        document["tanggal"] = laporan.tanggal
        document["pemasukan"] = laporan.pemasukan
        document["pengeluaran"] = laporan.pengeluaran
        document["photoLaporan"] = laporan.photoLaporan
        return document
    }

}