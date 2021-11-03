package com.example.s205350lykkehjulet.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.s205350lykkehjulet.Data.LetterCard
import com.example.s205350lykkehjulet.R
import com.google.android.material.card.MaterialCardView

class ItemAdapter(private var letterCardList: List<LetterCard>) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val letterTextView: TextView = itemView.findViewById(R.id.text_card_letter)
        private val cardView: MaterialCardView = itemView.findViewById(R.id.cardview_letter_card)

        fun bind(letterCard: LetterCard) {
            if (letterCard.letter == ' ') {
                cardView.visibility = View.INVISIBLE
            } else if (!letterCard.isHidden) {
                letterTextView.text = letterCard.letter.toString()
                cardView.setCardBackgroundColor(Color.WHITE)
            } else if (letterCard.isHidden) {
                letterTextView.text = " "
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(letterCardList[position])
    }

    override fun getItemCount(): Int {
        return if (letterCardList.isNullOrEmpty()) 0
        else letterCardList.size
    }
}