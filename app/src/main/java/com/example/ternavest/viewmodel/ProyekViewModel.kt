package com.example.ternavest.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ternavest.model.Proyek
import com.example.ternavest.repository.ProyekRepository

class ProyekViewModel : ViewModel() {
    private val repository = ProyekRepository()

    fun getResultByUUID()  = repository.getResultsByUUID()
    fun loadResultByUUID(uuid : String) = repository.getProyekByUUID(uuid)
    fun getResultByID()  = repository.getResultsByID()
    fun loadResultByID(id : String) = repository.getProyekByID(id)
    fun insert(proyek: Proyek?) { repository.insert(proyek!!) }
    fun update(proyek: Proyek?){ repository.update(proyek!!) }
    fun delete(idProyek : String){ repository.delete(idProyek)}
}