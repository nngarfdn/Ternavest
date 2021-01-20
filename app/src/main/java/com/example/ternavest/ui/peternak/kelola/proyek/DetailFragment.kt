package com.example.ternavest.ui.peternak.kelola.proyek

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ternavest.R
import com.example.ternavest.adapter.recycler.PeminatAdapter
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
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.view.*

class DetailFragment : BottomSheetDialogFragment(), ProfileCallback {
    // TODO: Rename and change types of parameters
    private val TAG = "DetailFragment"
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var proyekViewModel: ProyekViewModel
    private lateinit var portfolioViewModel: PortfolioViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private var listProfile : ArrayList<Profile> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

            for (porto in portfolioList) {
                profileViewModel.loadData(porto.investorId)
                profileViewModel.data.observe(this, { profil ->
                    listProfile.add(profil)
                    val b = listProfile.distinct()
                    Log.d(TAG, "onCreateView: load profile ${profil.name}")
                    Log.d(TAG, "onCreateView: list profile $b")

                    if (b.isNotEmpty()){
                        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL ,false)
                        rv_peminat.setLayoutManager(layoutManager)
                        val adapter = PeminatAdapter(b)
                        rv_peminat.setAdapter(adapter)
                        txtPeminatKosong.visibility = View.INVISIBLE
                    } else {
                        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL ,false)
                        rv_peminat.setLayoutManager(layoutManager)
                        val adapter = PeminatAdapter(b)
                        rv_peminat.setAdapter(adapter)
                        txtPeminatKosong.visibility = View.VISIBLE
                    }

                })
            }

        })

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


    override fun onFinish(listItem: ArrayList<Profile>) {
        for (item in listItem) {
            Log.d(TAG, "onFinish: nama peminat ${item?.name}")
        }
    }

}

internal interface ProfileCallback {
    fun onFinish(listItem: ArrayList<Profile>)
}