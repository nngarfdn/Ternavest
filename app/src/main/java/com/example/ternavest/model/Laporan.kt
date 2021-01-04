package com.example.ternavest.model

data class Laporan(
        var id : String? = "",
        var idProyek : String? = "",
        var judulLaporan : String? = "",
        var deskripsiLaporan : String? = "",
        var tanggal : String? = "",
        var pemasukan  : Int? = 0,
        var pengeluaran : Int? = 0,
        var photoLaporan : String? = ""
)