package com.example.s205350lykkehjulet.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.s205350lykkehjulet.Data.LetterCard
import com.example.s205350lykkehjulet.R

class ItemAdapter(private var letterCardList: List<LetterCard>?) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        //TODO ikke brug findview by id (pass context som param?)
        private val letterTextView: TextView = itemView.findViewById(R.id.letter_textView)
        private val cardView: CardView = itemView.findViewById(R.id.letter_cardView)

        fun bind(letterCard: LetterCard) {
            letterTextView.text = letterCard.letter.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
    : ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(letterCardList?.get(position)!!)
    }

    override fun getItemCount(): Int {
        return if (letterCardList.isNullOrEmpty()) 0
        else letterCardList!!.size
    }

    //fun updateItems(letterList: CharArray){
    //    //TODO skal det her ligge her, og hvordan gør man?
    //    for(i in this.letterCardList?.indices!!) {
    //        if (this.letterCardList[i] != letterList[i]) this.letterCardList[i] = letterList[i]
    //    }
    //    notifyDataSetChanged()
    //}
}