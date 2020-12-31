package com.example.ternavest.ui.peternak.kelola

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ternavest.R
import com.example.ternavest.model.Proyek
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_detail.view.*

class DetailFragment : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private val TAG = "DetailFragment"
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        val p : Proyek? = arguments?.getParcelable("proyek")

        view.txtTitle.setText(p?.namaProyek)
        view.txtDescription.setText(p?.deskripsiProyek)

        view.imgEditProyek.setOnClickListener{
            val intent = Intent(context,EditProyekActivity::class.java)
            intent.putExtra("proyek",p)
            startActivity(intent)}
        view.imgLaporanProyek.setOnClickListener{startActivity(Intent(context,LaporanActivity::class.java))}
        return view
    }



}