package com.example.ternavest.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ternavest.model.Proyek
import com.example.ternavest.repository.ProyekRepository

class ProyekViewModel : ViewModel() {
    private val repository = ProyekRepository()

    fun getResultByUUID()  = repository.getResultsByUUID()
    fun loadResultByUUID(uuid : String) = repository.getProyekByUUID(uuid)

    fun insert(proyek: Proyek?) { repository.insert(proyek!!) }
}