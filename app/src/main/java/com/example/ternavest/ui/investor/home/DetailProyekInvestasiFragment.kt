package com.example.ternavest.ui.investor.home

import android.annotation.SuppressLint
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
import com.example.ternavest.adapter.recycler.PeminatAdapter
import com.example.ternavest.model.Portfolio
import com.example.ternavest.model.Profile
import com.example.ternavest.model.Proyek
import com.example.ternavest.ui.investor.portfolio.AddUpdatePortfolioActivity
import com.example.ternavest.ui.both.portfolio.DetailPortfolioActivity.EXTRA_PROJECT
import com.example.ternavest.ui.both.profile.DetailProfileActivity
import com.example.ternavest.ui.both.profile.DetailProfileActivity.EXTRA_PROFILE
import com.example.ternavest.utils.AppUtils.LEVEL_INVESTOR
import com.example.ternavest.utils.AppUtils.LEVEL_PETERNAK
import com.example.ternavest.utils.DateUtils.getFullDate
import com.example.ternavest.viewmodel.PortfolioViewModel
import com.example.ternavest.viewmodel.ProfileViewModel
import com.example.ternavest.viewmodel.ProyekViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_detail_proyek_investasi.*
import kotlinx.android.synthetic.main.fragment_detail_proyek_investasi.view.*

class DetailProyekInvestasiFragment : BottomSheetDialogFragment() {
    companion object{
        private const val TAG = "DetailProyekInvestasiFr"
    }

    private var database: FirebaseFirestore? = null

    private lateinit var proyekViewModel: ProyekViewModel
    private lateinit var portfolioViewModel: PortfolioViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private var listProfile : ArrayList<Profile> = ArrayList()
    private var profilePeternak: Profile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseFirestore.getInstance()
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detail_proyek_investasi, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        rv_peminat.layoutManager = layoutManager
        val adapter = PeminatAdapter(listProfile)
        rv_peminat.adapter = adapter

        val p: Proyek? = arguments?.getParcelable("proyek")

        proyekViewModel = ViewModelProvider(this, NewInstanceFactory()).get(ProyekViewModel::class.java)
        portfolioViewModel = ViewModelProvider(this, NewInstanceFactory()).get(PortfolioViewModel::class.java)
        profileViewModel = ViewModelProvider(this, NewInstanceFactory()).get(ProfileViewModel::class.java)

        portfolioViewModel.queryPeminat(p?.id)
        portfolioViewModel.data.observe(this, Observer<ArrayList<Portfolio>> { portfolioList ->
            for (porto in portfolioList) profileViewModel.loadData(porto.investorId) // Muat profil investor/peminat
        })
        profileViewModel.data.observe(this, Observer<Profile> { profil ->
            if (profil.level.equals(LEVEL_INVESTOR)){
                listProfile.add(profil)
                adapter.notifyDataSetChanged()

                val b = listProfile.distinct()
                Log.d(TAG, "onCreateView: load profile ${profil.name}")
                Log.d(TAG, "onCreateView: list profile $b")

                if (b.isNotEmpty()) txtPeminatKosong.visibility = View.INVISIBLE
                else txtPeminatKosong.visibility = View.VISIBLE
            } else if (profil.level.equals(LEVEL_PETERNAK)){
                Log.d("DetailInvestor", "onViewCreated: ${profil.photo} ")
                profilePeternak = profil
                Picasso.get()
                        .load(profil.photo)
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.ic_no_profile_pic)
                        .error(R.drawable.ic_no_profile_pic)
                        .into(view.imgProfile)
            }
        })
        profileViewModel.loadData(p?.uuid) // Muat profil peternak

        view.txtTitle.text = p?.namaProyek
        view.txtDeskripsi.text = p?.deskripsiProyek
        view.txtJenisHewanDetail.text = p?.jenisHewan
        view.txtRoiDetail.text = "${p?.roi}%"
        view.txtTanggalDetail.text = "${getFullDate(p?.waktuMulai, true)} s.d. ${getFullDate(p?.waktuSelesai, true)}"
        view.txtAlamatLengkap.text = p?.alamatLengkap + ", " + p?.kecamatan + ", " + p?.kabupaten + ", " + p?.provinsi

        Picasso.get()
                .load(p?.photoProyek)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.load_image)
                .into(view.imgDeskripsiLaporan)

        view.imgProfile.setOnClickListener {
            if (profilePeternak == null) return@setOnClickListener
            val intent = Intent(context, DetailProfileActivity::class.java)
            intent.putExtra(EXTRA_PROFILE, profilePeternak)
            startActivity(intent)
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