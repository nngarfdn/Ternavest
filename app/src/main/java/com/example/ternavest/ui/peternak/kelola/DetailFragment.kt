package com.example.ternavest.ui.peternak.kelola

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        val proyek : Proyek? = arguments?.getParcelable("proyek")

        Log.d(TAG, "onCreateView: $proyek")

        view.tvTitle.setText(proyek?.namaProyek)
        Toast.makeText(context, "${proyek?.namaProyek}", Toast.LENGTH_SHORT).show()
        return view
    }



}