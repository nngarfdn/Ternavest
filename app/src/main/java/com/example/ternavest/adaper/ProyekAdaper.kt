package com.example.ternavest.adaper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ternavest.R
import com.example.ternavest.model.Proyek
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_proyek.view.*

class ProyekAdaper (private val list: List<Proyek>) : RecyclerView.Adapter<ProyekAdaper.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_proyek, parent, false))

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Picasso.get()
                .load(list?.get(position)?.photoProyek)
                .resize(100, 100) // resizes the image to these dimensions (in pixel)
                .centerCrop()
                .placeholder(R.drawable.upload)
                .into(holder.itemView.imgProyek)

        holder.itemView.txtNamaProyek.text = list[position].namaProyek

        holder.itemView.setOnClickListener {

        }
    }
}