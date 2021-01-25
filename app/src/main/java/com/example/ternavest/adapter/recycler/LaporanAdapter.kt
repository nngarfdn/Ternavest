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
import com.example.ternavest.utils.DateUtils.getFullDate
import kotlinx.android.synthetic.main.item_laporan.view.*

class LaporanAdapter (private val list: List<Laporan>) : RecyclerView.Adapter<LaporanAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_laporan, parent, false))

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.itemView.txtJudulLaporan.text = list[position].judulLaporan
        holder.itemView.txtTanggalLaporan.text = getFullDate(list[position].tanggal, false)

        holder.itemView.setOnClickListener {
            val args = Bundle()
            args.putParcelable("laporan", list[position])
            val bottomSheet = DetailLaporanDialogFragment()
            bottomSheet.arguments = args
            bottomSheet.show((holder.itemView.context as FragmentActivity).supportFragmentManager, bottomSheet.tag)
        }
    }
}