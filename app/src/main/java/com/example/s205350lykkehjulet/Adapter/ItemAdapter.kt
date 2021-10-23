package com.example.s205350lykkehjulet.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.s205350lykkehjulet.R

class ItemAdapter(private var letterList: CharArray) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        //TODO ikke brug findview by id (pass context som param?)
        private val letterTextView: TextView = itemView.findViewById(R.id.letterTextView)

        fun bind(letter: Char){
            letterTextView.text = letter.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
    : ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(letterList[position])
    }

    override fun getItemCount() = letterList.size

    fun updateItems(letterList: CharArray){
        //TODO skal det her ligge her?
        for(i in this.letterList.indices) {
            if (this.letterList[i] != letterList[i]) this.letterList[i] = letterList[i]
        }
        notifyDataSetChanged()
    }
}