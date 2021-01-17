package com.example.ternavest.ui.peternak.kelola.proyek

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.example.ternavest.R
import com.example.ternavest.model.Profile
import com.example.ternavest.model.Proyek
import com.example.ternavest.ui.peternak.kelola.laporan.LaporanActivity
import com.example.ternavest.utils.AppUtils.LEVEL_PETERNAK
import com.example.ternavest.viewmodel.PortfolioViewModel
import com.example.ternavest.viewmodel.ProfileViewModel
import com.example.ternavest.viewmodel.ProyekViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.fragment_detail.view.imgDeskripsiLaporan
import kotlinx.android.synthetic.main.fragment_detail.view.imgLaporanProyek
import kotlinx.android.synthetic.main.fragment_detail.view.imgProfile
import kotlinx.android.synthetic.main.fragment_detail.view.txtDeskripsi
import kotlinx.android.synthetic.main.fragment_detail.view.txtJenisHewanDetail
import kotlinx.android.synthetic.main.fragment_detail.view.txtPeminat
import kotlinx.android.synthetic.main.fragment_detail.view.txtRoiDetail
import kotlinx.android.synthetic.main.fragment_detail.view.txtTanggalDetail
import kotlinx.android.synthetic.main.fragment_detail.view.txtTitle
import kotlinx.android.synthetic.main.fragment_detail_proyek_investasi.view.*
import java.util.*
import kotlin.collections.ArrayList

class DetailFragment : BottomSheetDialogFragment(), PeminatCallback {
    // TODO: Rename and change types of parameters
    private val TAG = "DetailFragment"
    private var param1: String? = null
    private var param2: String? = null
    private var database: FirebaseFirestore? = null


    private lateinit var proyekViewModel: ProyekViewModel
    private lateinit var portfolioViewModel: PortfolioViewModel
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseFirestore.getInstance()
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        val p: Proyek? = arguments?.getParcelable("proyek")


        proyekViewModel = ViewModelProvider(this, NewInstanceFactory()).get(ProyekViewModel::class.java)
        portfolioViewModel = ViewModelProvider(this, NewInstanceFactory()).get(PortfolioViewModel::class.java)
        profileViewModel = ViewModelProvider(this, NewInstanceFactory()).get(ProfileViewModel::class.java)

        portfolioViewModel.queryPeminat(p?.id)
        portfolioViewModel.data.observe(this, { portfolioList ->

            var nama: ArrayList<String> = ArrayList()
            for (portfolio in portfolioList) {
                profileViewModel.loadData(portfolio.investorId)
                nama.clear()
                profileViewModel.data.observe(this, { profile ->
                    nama.add(profile.name)
                    Log.d(TAG, "onCreateView nama: ${profile.name}")

                    val b = nama.distinct()

                    Log.d(TAG, "onCreateView: anggota $b")
                })

            }


        })

        var listProfile: MutableList<String?>? = mutableListOf()

        val listProductId = p?.peminat

        view.txtPeminat.setText(listProfile.toString())


        view.txtTitle.setText(p?.namaProyek)
        view.txtDeskripsi.setText(p?.deskripsiProyek)
        view.txtJenisHewanDetail.setText(p?.jenisHewan)
        view.txtRoiDetail.setText("${p?.roi}%")
        view.txtTanggalDetail.setText("${p?.waktuMulai} - ${p?.waktuSelesai}")

//        view.btnInvestasiSekarang.visibility = View.INVISIBLE

        Picasso.get()
                .load(p?.photoProyek)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.load_image)
                .into(view.imgDeskripsiLaporan)

        view.imgProfile.setOnClickListener {
            val intent = Intent(context, EditProyekActivity::class.java)
            intent.putExtra("proyek", p)
            startActivity(intent)
        }
        view.imgLaporanProyek.setOnClickListener {
            val intent = Intent(context, LaporanActivity::class.java)
            intent.putExtra("level", LEVEL_PETERNAK)
            intent.putExtra("id", p?.id)
            startActivity(intent)
        }

        return view
    }


    override fun onFinish(listItem: ArrayList<Profile>?) {
        val listNama: ArrayList<String> = ArrayList<String>()

        if (listItem != null) {
            for (profil in listItem) {
                listNama
            }
        }

    }


}