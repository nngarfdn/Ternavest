package com.example.ternavest.adapter.recycler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.ternavest.R
import com.example.ternavest.model.Proyek
import com.example.ternavest.ui.investor.home.DetailProyekInvestasiFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_proyek.view.*
import java.util.*
import kotlin.collections.ArrayList

class ProyekInvestorAdapter (private val list: List<Proyek>) : RecyclerView.Adapter<ProyekInvestorAdapter.ViewHolder>(), Filterable {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    var countryFilterList = ArrayList<Proyek>()

    init {
        countryFilterList = list as ArrayList<Proyek>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_proyek, parent, false))

    override fun getItemCount(): Int = countryFilterList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Picasso.get()
                .load(list?.get(position)?.photoProyek)
                .resize(100, 100) // resizes the image to these dimensions (in pixel)
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_autorenew_24)
                .into(holder.itemView.imgProyek)

        holder.itemView.txtNamaProyek.text = list[position].namaProyek
        holder.itemView.txtJenisHewan.text = list[position].jenisHewan
        holder.itemView.txtROI.text = "${list[position].roi} %"

        holder.itemView.setOnClickListener {
            val args = Bundle()
            args.putParcelable("proyek",list.get(position))
            val bottomSheet = DetailProyekInvestasiFragment()
            bottomSheet.setArguments(args)
            bottomSheet.show((holder.itemView.context as FragmentActivity).supportFragmentManager, bottomSheet.getTag())
        }
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()){
                    countryFilterList = list as ArrayList<Proyek>
                } else {
                    val resultList = ArrayList<Proyek>()
                    for (row in list){
                        if (row.namaProyek?.toLowerCase(Locale.ROOT)?.contains(charSearch.toLowerCase(Locale.ROOT))!!){
                            resultList.add(row)
                        }
                    }
                    countryFilterList = resultList
                }
                val filterResult = FilterResults()
                filterResult.values = countryFilterList
                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                countryFilterList = results?.values as ArrayList<Proyek>
                notifyDataSetChanged()
            }

        }
    }
}