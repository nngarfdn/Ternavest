package com.example.ternavest.ui.peternak.kelola.laporan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ternavest.R
import com.example.ternavest.model.Laporan
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_detail_laporan_dialog.view.*


class DetailLaporanDialogFragment : BottomSheetDialogFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_detail_laporan_dialog, container, false)
        val p: Laporan? = arguments?.getParcelable("laporan")

        view.txtTitle.setText( p?.judulLaporan)
        view.txtDeskripsi.setText(p?.deskripsiLaporan)
        view.txtJenisHewanDetail.setText("Rp${p?.pemasukan}")
        view.txtRoiDetail.setText("Rp${p?.pengeluaran}")

        Picasso.get()
                .load(p?.photoLaporan) // resizes the image to these dimensions (in pixel)
                .placeholder(R.drawable.load_image)
                .into(view.imgDeskripsiLaporan)

        view.imgProfile.setOnClickListener {
            val intent = Intent(context, EditLaporanActivity::class.java)
            intent.putExtra("laporan", p)
            startActivity(intent)
        }
        // Inflate the layout for this fragment
        return view
    }
}

