package com.example.s205350lykkehjulet.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.s205350lykkehjulet.models.LetterCard
import com.example.s205350lykkehjulet.R
import com.google.android.material.card.MaterialCardView

/**
 * Custom adapter for holding LetterCards
 *
 * @property letterCardList a list of LetterCards
 */
class LetterCardAdapter(private var letterCardList: List<LetterCard>) :
    RecyclerView.Adapter<LetterCardAdapter.ViewHolder>() {

    /**
     * Responsible for holding the view
     *
     *@property viewLetterCard the item view
     */
    class ViewHolder(viewLetterCard: View) : RecyclerView.ViewHolder(viewLetterCard) {
        private val letterTextView: TextView = viewLetterCard.findViewById(R.id.text_card_letter)
        private val cardView: MaterialCardView = viewLetterCard.findViewById(R.id.card_view_letter_cards)

        /**
         * Responsible controlling how the LetterCard appears
         *
         * @param letterCard the current lettercard
         */
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
        val viewLetterCard = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_letter_card, parent, false)
        return ViewHolder(viewLetterCard)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(letterCardList[position])
    }

    override fun getItemCount(): Int {
        return if (letterCardList.isNullOrEmpty()) 0
        else letterCardList.size
    }
}