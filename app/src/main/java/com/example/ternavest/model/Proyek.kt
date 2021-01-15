package com.example.ternavest.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Proyek(
        var id: String? = "",
        val uuid: String? = "",
        var namaProyek: String? = "",
        var deskripsiProyek: String? = "",
        var jenisHewan: String? = "",
        var roi: Int? = 0,
        var waktuMulai: String? = "",
        var waktuSelesai: String? = "",
        var biayaHewan: Long? = 0,
        var provinsi: String? = "",
        var kabupaten: String? = "",
        var kecamatan: String? = "",
        var alamatLengkap: String? = "",
        var photoProyek: String? = "",
        var peminat: List<String?>? = null
) : Parcelable