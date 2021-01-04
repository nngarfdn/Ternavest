package com.example.ternavest.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ternavest.model.Laporan
import com.example.ternavest.model.Proyek
import com.example.ternavest.repository.LaporanRepository

class LaporanViewModel : ViewModel(){

    private val repository = LaporanRepository()

    fun getResultByID()  = repository.getResultsByID()
    fun loadResultByID(id : String) = repository.getProyekByID(id)
    fun getResultByProyekID()  = repository.getResultsByProyekID()
    fun loadResultByProyekID(idProyek : String) = repository.getLaporanByProyekID(idProyek)
    fun insert(laporan: Laporan?) { repository.insert(laporan!!) }
    fun update(laporan: Laporan?){ repository.update(laporan!!) }
    fun delete(idProyek : String){ repository.delete(idProyek)}
}