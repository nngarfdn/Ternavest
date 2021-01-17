package com.example.ternavest.adapter.recycler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.ternavest.R
import com.example.ternavest.model.Laporan
import com.example.ternavest.model.Profile
import com.example.ternavest.ui.peternak.kelola.laporan.DetailLaporanDialogFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_laporan.view.*
import kotlinx.android.synthetic.main.item_peminat.view.*
import kotlinx.android.synthetic.main.item_proyek.view.*

class PeminatAdaper (private val list: List<Profile>) : RecyclerView.Adapter<PeminatAdaper.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_peminat, parent, false))

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Picasso.get()
                .load(list?.get(position)?.photo)
                .resize(39, 39) // resizes the image to these dimensions (in pixel)
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_autorenew_24)
                .into(holder.itemView.imgProfile)

        holder.itemView.setOnClickListener {

        }
    }
}