package com.enescanpolat.googlemapskotlin.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.enescanpolat.googlemapskotlin.databinding.RecyclerRowBinding
import com.enescanpolat.googlemapskotlin.model.place
import com.enescanpolat.googlemapskotlin.view.MapsActivity

class placeAdapter(val placeList : List<place>):RecyclerView.Adapter<placeAdapter.Placeholder>() {

    class Placeholder(val recylerRowBinding: RecyclerRowBinding):RecyclerView.ViewHolder(recylerRowBinding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Placeholder {
        val recylerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Placeholder(recylerRowBinding)
    }

    override fun onBindViewHolder(holder: Placeholder, position: Int) {
        holder.recylerRowBinding.recyclerViewTextView.text=placeList.get(position).name
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context,MapsActivity::class.java)
            intent.putExtra("selectedPlace",placeList.get(position))
            intent.putExtra("info","old")
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return placeList.size
    }

}