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
        val playerInputLetter = binding.LetterInput.text?.firstOrNull()

        //PlayerInputLetter cannot be null beyond this point
        playerInputLetter ?: return

        //Resets LetterInput field
        binding.LetterInput.setText("")

        if (viewModel.isUserInputMatch(playerInputLetter)) {
            updateWordToBeGuessedOnScreen()
            updateLuckyWheelResult()
            updateScore()
            updateLives()
            setErrorTextField(false)
        } else {
            updateLives()
            updateLuckyWheelResult()
            updateScore()
            setErrorTextField(true)
            if (viewModel.lives<=0) {
                endGame()
            }
        }
    }

    private fun updateLives() {
        binding.Lives.text = getString(R.string.lives, viewModel.lives.toString())
    }

    private fun updateScore() {
        binding.Score.text = getString(R.string.score, viewModel.score.toString())
    }

    private fun endGame() {
        TODO("Not yet implemented")
    }

    private fun updateWordToBeGuessedOnScreen() {
        binding.WordToBeGuessed.text = viewModel.shownWordToBeGuessed
    }

    private fun updateLuckyWheelResult(){
        binding.WheelResult.text = viewModel.wheelResult
    }

    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.LetterInput.error = "Oh no! The word does not contain a \"${viewModel.lastGuessedChar}\""
        } else {
            binding.textField.isErrorEnabled = false
            binding.LetterInput.text = null
        }
    }
}