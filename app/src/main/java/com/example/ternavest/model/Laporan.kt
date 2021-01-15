package com.example.ternavest.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Laporan(
        var id : String? = "",
        var idProyek : String? = "",
        var judulLaporan : String? = "",
        var deskripsiLaporan : String? = "",
        var tanggal : String? = "",
        var pemasukan  : Long? = 0,
        var pengeluaran : Long? = 0,
        var photoLaporan : String? = ""
) : Parcelable