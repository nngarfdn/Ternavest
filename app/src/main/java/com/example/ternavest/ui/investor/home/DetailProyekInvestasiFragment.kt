package com.example.ternavest.ui.investor.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.example.ternavest.R
import com.example.ternavest.model.Proyek
import com.example.ternavest.ui.peternak.kelola.laporan.LaporanActivity
import com.example.ternavest.ui.peternak.kelola.proyek.EditProyekActivity
import com.example.ternavest.viewmodel.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_detail_proyek_investasi.view.*

class DetailProyekInvestasiFragment : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_proyek_investasi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel = ViewModelProvider(this, NewInstanceFactory()).get(ProfileViewModel::class.java)
        val p: Proyek? = arguments?.getParcelable("proyek")

        view.txtTitle.setText(p?.namaProyek)
        view.txtDeskripsi.setText(p?.deskripsiProyek)
        view.txtJenisHewanDetail.setText(p?.jenisHewan)
        view.txtRoiDetail.setText("${p?.roi}%")
        view.txtTanggalDetail.setText("${p?.waktuMulai} - ${p?.waktuSelesai}")

        Picasso.get()
                .load(p?.photoProyek)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.load_image)
                .into(view.imgDeskripsiLaporan)


        profileViewModel.loadData(p?.uuid)
        profileViewModel.data.observe(viewLifecycleOwner, Observer { result ->
            Log.d("DetailInvestor", "onViewCreated: ${result.photo} ")
            Picasso.get()
                    .load(result.photo)
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.load_image)
                    .into(view.imgProfile)
        })

        view.imgProfile.setOnClickListener {
            val intent = Intent(context, EditProyekActivity::class.java)
            intent.putExtra("proyek", p)
            startActivity(intent)
        }
        view.imgLaporanProyek.setOnClickListener {
            val intent = Intent(context, LaporanActivity::class.java)
            intent.putExtra("id", p?.id)
            startActivity(intent)
        }
    }

}