package com.example.ternavest.adapter.recycler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.ternavest.R
import com.example.ternavest.model.Laporan
import com.example.ternavest.ui.peternak.kelola.laporan.DetailLaporanDialogFragment
import kotlinx.android.synthetic.main.item_laporan.view.*

class LaporanAdaper (private val list: List<Laporan>) : RecyclerView.Adapter<LaporanAdaper.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_laporan, parent, false))

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.itemView.txtJudulLaporan.text = list[position].judulLaporan
        holder.itemView.txtTanggalLaporan.text = list[position].tanggal

        holder.itemView.setOnClickListener {
            val args = Bundle()
            args.putParcelable("laporan",list.get(position))
            val bottomSheet = DetailLaporanDialogFragment()
            bottomSheet.setArguments(args)
            bottomSheet.show((holder.itemView.context as FragmentActivity).supportFragmentManager, bottomSheet.getTag())
        }
    }
}