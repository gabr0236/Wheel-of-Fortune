package com.example.s205350lykkehjulet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.s205350lykkehjulet.databinding.GameFragmentBinding


class GameFragment : Fragment() {
    private lateinit var binding: GameFragmentBinding

    private val viewModel: GameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        print("Binding")
        // Inflate the layout XML file and return a binding object instance
        binding = inflate(inflater, R.layout.game_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the viewModel for data binding - this allows the bound layout access
        // to all the data in the VieWModel
        binding.gameViewModel = viewModel
        // Specify the fragment view as the lifecycle owner of the binding.
        // This is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        // Setup a click listener for the Submit
        binding.GuessButton.setOnClickListener { submitGuess() }
    }

    private fun submitGuess() {
        val playerInputLetter = binding.LetterInput.text.toString()
        if (viewModel.isUserImputMatch(playerInputLetter[0])) {
            updateWordToBeGuessedOnScreen()
            updateLuckyWheelResult()
        } else {
            //setErrorTextField(true)
            //TODO: fix vvv (value!!)
            if (viewModel.lives <=0) {
                endGame()
            }
        }
    }

    private fun endGame() {
        TODO("Not yet implemented")
    }

    private fun setErrorTextField(b: Boolean) {
    //TODO: implement (prøv lambda?)
    }

    private fun updateWordToBeGuessedOnScreen() {
        binding.WordToBeGuessed.text = viewModel.shownWordToBeGuessed
    }

    private fun updateLuckyWheelResult(){
        binding.WheelResult.text = viewModel.wheelResult
    }
}