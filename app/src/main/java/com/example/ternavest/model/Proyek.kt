package com.example.ternavest.model

data class Proyek(
        var namaProyek : String? = "",
        var deskripsiProyek : String? = "",
        var jenisHewan : String? = "",
        var roi : String? = "",
        var waktuMulai : String? = "",
        var waktuSelesai : String? = "",
        var biayaHewan : Int? = null,
        var provinsi : String? = "",
        var kabupaten : String? = "",
        var kecamatan : String? = "",
        var alamatLengkap : String? = "",
        var photoProyek : String? = ""
)