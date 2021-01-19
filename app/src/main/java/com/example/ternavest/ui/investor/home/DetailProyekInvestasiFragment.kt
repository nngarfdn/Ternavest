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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ternavest.R
import com.example.ternavest.adapter.recycler.LaporanAdaper
import com.example.ternavest.adapter.recycler.PeminatAdaper
import com.example.ternavest.model.Profile
import com.example.ternavest.model.Proyek
import com.example.ternavest.ui.both.portfolio.AddUpdatePortfolioActivity
import com.example.ternavest.ui.both.portfolio.DetailPortfolioActivity.EXTRA_PROJECT
import com.example.ternavest.ui.both.profile.DetailProfileActivity
import com.example.ternavest.ui.both.profile.DetailProfileActivity.EXTRA_PROFILE
import com.example.ternavest.ui.peternak.kelola.laporan.LaporanActivity
import com.example.ternavest.ui.peternak.kelola.proyek.EditProyekActivity
import com.example.ternavest.viewmodel.PortfolioViewModel
import com.example.ternavest.viewmodel.ProfileViewModel
import com.example.ternavest.viewmodel.ProyekViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_laporan.*
import kotlinx.android.synthetic.main.fragment_detail_proyek_investasi.*
import kotlinx.android.synthetic.main.fragment_detail_proyek_investasi.view.*

class DetailProyekInvestasiFragment : BottomSheetDialogFragment() {

    private var database: FirebaseFirestore? = null
    private val TAG = "DetailProyekInvestasiFr"

    private lateinit var proyekViewModel: ProyekViewModel
    private lateinit var portfolioViewModel: PortfolioViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private var listProfile : ArrayList<Profile> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseFirestore.getInstance()
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
                        val adapter = PeminatAdaper(b)
                        rv_peminat.setAdapter(adapter)
                        txtPeminatKosong.visibility = View.INVISIBLE
                    } else {
                        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL ,false)
                        rv_peminat.setLayoutManager(layoutManager)
                        val adapter = PeminatAdaper(b)
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
            profileViewModel.data.observe(viewLifecycleOwner, Observer { result ->
                Log.d("DetailInvestor", "onViewCreated: ${result.photo} ")
                val intent = Intent(context, DetailProfileActivity::class.java)
                intent.putExtra(EXTRA_PROFILE, result)
                startActivity(intent)
            })

        }
        view.imgLaporanProyek.setOnClickListener {
            val intent = Intent(context, LaporanHomeActivity::class.java)
            intent.putExtra("level", "investor")
            intent.putExtra("id", p?.id)
            startActivity(intent)
        }

        view.btnInvestasiSekarang.setOnClickListener {
            val intent = Intent(context, AddUpdatePortfolioActivity::class.java)
            intent.putExtra(EXTRA_PROJECT, p)
            startActivity(intent)
        }
    }

}