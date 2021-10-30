package com.example.s205350lykkehjulet.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.s205350lykkehjulet.data.LetterCard
import com.example.s205350lykkehjulet.R
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.card.MaterialCardView

class ItemAdapter(private var letterCardList: List<LetterCard>?) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //TODO ikke brug findview by id (pass context som param?)
        private val letterTextView: TextView = itemView.findViewById(R.id.letter_textView)
        private val cardView: MaterialCardView = itemView.findViewById(R.id.letter_card_view)
        //private val flexboxLayout: FlexboxLayout = itemView.findViewById(R.id.letter_card_layout)
        //private var lp = flexboxLayout.layoutParams as FlexboxLayout.LayoutParams

        fun bind(letterCard: LetterCard) {
            if (letterCard.letter == ' ') {
                cardView.visibility = View.INVISIBLE
             //       lp.isWrapBefore=true
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
        holder.bind(letterCardList?.get(position)!!)
        Log.d("Adapter", "bind called")
    }

    override fun getItemCount(): Int {
        return if (letterCardList.isNullOrEmpty()) 0
        else letterCardList!!.size
    }
}