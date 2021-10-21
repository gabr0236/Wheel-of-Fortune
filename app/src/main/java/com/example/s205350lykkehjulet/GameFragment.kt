package com.example.s205350lykkehjulet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.s205350lykkehjulet.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class GameFragment : Fragment() {
    private lateinit var binding: GameFragmentBinding

    private val viewModel: GameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout XML file and return a binding object instance
        val fragmentBinding = GameFragmentBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.gameFragment = this

        // Set the viewModel for data binding - this allows the bound layout access
        // to all the data in the VieWModel
        binding.gameViewModel = viewModel
        // Specify the fragment view as the lifecycle owner of the binding.
        // This is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        // Setup a click listener for the Submit
        binding.GuessButton.setOnClickListener { submitGuessAndSpinWheel() }
        updateGameQuote()
        updateCategory()
    }

    private fun submitGuessAndSpinWheel() {
        val playerInputLetter = binding.LetterInput.text?.firstOrNull()

        //PlayerInputLetter cannot be null beyond this point
        //TODO add this throughout the code
        playerInputLetter ?: return

        //Resets LetterInput field
        binding.LetterInput.setText("")

        if (viewModel.isUserInputMatch(playerInputLetter)) {
            viewModel.doWheelAction()
            setErrorTextField(false)

            if (viewModel.isWon) {
                findNavController().navigate(R.id.action_gameFragment_to_gameWonFragment)
                return
            }
            viewModel.spinLuckyWheel()
            if (!viewModel.wheelResult.isDigitsOnly()) {
                showJokerDialog()
            }
            updateView()
        } else {
            if (viewModel.lives <= 0) {
                findNavController().navigate(R.id.action_gameFragment_to_gameLostFragment)
                return
            }
            viewModel.spinLuckyWheel()
            updateView()
            setErrorTextField(true)
        }
    }

    private fun updateView(){
        updateLives()
        updateLuckyWheelResult()
        updateScore()
        updateGameQuote()
        updateWordToBeGuessedOnScreen()
    }

    private fun updateGameQuote() {
        binding.GameQuote.text =
            String.format(resources.getString(R.string.game_quote), viewModel.wheelResult)
    }

    private fun showJokerDialog() {
        val message: String = when(viewModel.wheelResult) {
            "Miss Turn" -> "Dang! You rolled \"${viewModel.wheelResult}\" and lost a life"
            "Extra Turn" -> "Yay! You rolled \"${viewModel.wheelResult}\" and gained a life"
            "Bankrupt" -> "Dang! You rolled \"${viewModel.wheelResult}\" and lost a your points :("
            else -> "Error" //TODO: udenom det her? evt kast exception
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(viewModel.wheelResult)
            .setMessage(message)
            .setCancelable(true)
            .show()

        continueGameAfterJokerDialog()
    }

    private fun continueGameAfterJokerDialog() {
        viewModel.doWheelAction()
        if (viewModel.lives <= 0) {
            findNavController().navigate(R.id.action_gameFragment_to_gameLostFragment)
            return
        }
        viewModel.spinLuckyWheel()

        //In case of rolling this again
        if (viewModel.wheelResult == "Extra Turn"
            || viewModel.wheelResult == "Miss Turn"
            || viewModel.wheelResult == "Bankrupt") {
            showJokerDialog()
        }
    }

    private fun updateLives() {
        binding.Lives.text = getString(R.string.lives, viewModel.lives.toString())
    }

    private fun updateScore() {
        binding.Score.text = getString(R.string.score, viewModel.score.toString())
    }

    private fun updateWordToBeGuessedOnScreen() {
        binding.WordToBeGuessed.text = viewModel.shownWordToBeGuessed
    }

    private fun updateLuckyWheelResult() {
        binding.WheelResult.text = viewModel.wheelResult
    }

    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.LetterInput.error = "Oh no! Wrong Guess."
        } else {
            binding.textField.isErrorEnabled = false
            binding.LetterInput.text = null
        }
    }

    private fun updateCategory() {
        binding.Catagory.text =
            String.format(resources.getString(R.string.category), viewModel.category)
    }
}