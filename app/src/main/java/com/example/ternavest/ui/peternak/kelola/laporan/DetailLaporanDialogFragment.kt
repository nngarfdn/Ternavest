package com.example.ternavest.ui.peternak.kelola.laporan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ternavest.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class DetailLaporanDialogFragment : BottomSheetDialogFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_laporan_dialog, container, false)
    }

}