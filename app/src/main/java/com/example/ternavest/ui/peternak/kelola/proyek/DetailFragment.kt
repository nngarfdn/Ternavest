package com.example.ternavest.ui.peternak.kelola.proyek

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
import com.example.ternavest.ui.peternak.kelola.laporan.LaporanActivity
import com.example.ternavest.utils.AppUtils.LEVEL_PETERNAK
import com.example.ternavest.utils.DateUtils.getFullDate
import com.example.ternavest.viewmodel.PortfolioViewModel
import com.example.ternavest.viewmodel.ProfileViewModel
import com.example.ternavest.viewmodel.ProyekViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.view.imgDeskripsiLaporan
import kotlinx.android.synthetic.main.fragment_detail.view.imgLaporanProyek
import kotlinx.android.synthetic.main.fragment_detail.view.imgProfile
import kotlinx.android.synthetic.main.fragment_detail.view.txtAlamatLengkap
import kotlinx.android.synthetic.main.fragment_detail.view.txtDeskripsi
import kotlinx.android.synthetic.main.fragment_detail.view.txtJenisHewanDetail
import kotlinx.android.synthetic.main.fragment_detail.view.txtRoiDetail
import kotlinx.android.synthetic.main.fragment_detail.view.txtTanggalDetail
import kotlinx.android.synthetic.main.fragment_detail.view.txtTitle

class DetailFragment : BottomSheetDialogFragment(), ProfileCallback {
    companion object{
        private const val TAG = "DetailFragment"
    }

    private lateinit var proyekViewModel: ProyekViewModel
    private lateinit var portfolioViewModel: PortfolioViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private var listProfile : ArrayList<Profile> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL ,false)
        rv_peminat.layoutManager = layoutManager
        val adapter = PeminatAdapter(listProfile)
        rv_peminat.adapter = adapter

        val p: Proyek? = arguments?.getParcelable("proyek")

        proyekViewModel = ViewModelProvider(this, NewInstanceFactory()).get(ProyekViewModel::class.java)
        portfolioViewModel = ViewModelProvider(this, NewInstanceFactory()).get(PortfolioViewModel::class.java)
        profileViewModel = ViewModelProvider(this, NewInstanceFactory()).get(ProfileViewModel::class.java)

        portfolioViewModel.queryPeminat(p?.id)
        portfolioViewModel.data.observe(this, Observer<ArrayList<Portfolio>>{ portfolioList ->
            for (porto in portfolioList) profileViewModel.loadData(porto.investorId)
        })
        profileViewModel.data.observe(this, Observer<Profile>{ profil ->
            listProfile.add(profil)
            adapter.notifyDataSetChanged()

            val b = listProfile.distinct()
            Log.d(TAG, "onCreateView: load profile ${profil.name}")
            Log.d(TAG, "onCreateView: list profile $b")

            if (b.isNotEmpty()) txtPeminatKosong.visibility = View.INVISIBLE
            else txtPeminatKosong.visibility = View.VISIBLE
        })

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
    }

    override fun onFinish(listItem: ArrayList<Profile>) {
        for (item in listItem) {
            Log.d(TAG, "onFinish: nama peminat ${item.name}")
        }
    }
}

internal interface ProfileCallback {
    fun onFinish(listItem: ArrayList<Profile>)
}