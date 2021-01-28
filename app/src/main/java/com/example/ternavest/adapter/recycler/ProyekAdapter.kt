package com.example.ternavest.adapter.recycler

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.ternavest.R
import com.example.ternavest.model.Proyek
import com.example.ternavest.ui.peternak.kelola.proyek.DetailFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_proyek.view.*

class ProyekAdapter (private val list: List<Proyek>) : RecyclerView.Adapter<ProyekAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_proyek, parent, false))

    override fun getItemCount(): Int = list.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Picasso.get()
                .load(list[position].photoProyek)
                .resize(100, 100) // resizes the image to these dimensions (in pixel)
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_autorenew_24)
                .into(holder.itemView.imgProyek)

        holder.itemView.txtNamaProyek.text = list[position].namaProyek
//        holder.itemView.txtJenisHewan.text = list[position].jenisHewan
        holder.itemView.txtKotaProyek.text = list[position].kabupaten
        holder.itemView.txtROI.text = "ROI : ${list[position].roi}%"

        holder.itemView.setOnClickListener {
            val args = Bundle()
            args.putParcelable("proyek", list[position])
            val bottomSheet = DetailFragment()
            bottomSheet.arguments = args
            bottomSheet.show((holder.itemView.context as FragmentActivity).supportFragmentManager, bottomSheet.tag)
        }
    }
}