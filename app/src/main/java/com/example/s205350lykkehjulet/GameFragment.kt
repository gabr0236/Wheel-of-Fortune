package com.example.s205350lykkehjulet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil.inflate
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
    ): View? {
        // Inflate the layout XML file and return a binding object instance
        val fragmentBinding = GameFragmentBinding.inflate(inflater, container, false)
        binding=fragmentBinding
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
        binding.GuessButton.setOnClickListener { submitGuess() }
    }

    private fun submitGuess() {
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
            updateWordToBeGuessedOnScreen()
            updateLuckyWheelResult()
            updateScore()
            updateLives()
        } else {
            if (viewModel.lives<=0) {
                findNavController().navigate(R.id.action_gameFragment_to_gameLostFragment)
                return
            }
            updateLives()
            updateLuckyWheelResult()
            updateScore()
            setErrorTextField(true)
        }
    }

    private fun showJokerDialog(){
        when (viewModel.wheelResult) {
            "Miss Turn"  -> showMissTurnDialog()
            "Extra Turn" -> showExtraTurnDialog()
            "Bankrupt"   -> showBankruptDialog()
        }
        //TODO: de her ting skal ske i viewModel?
        viewModel.doWheelAction()
        if (viewModel.lives<=0) {
            findNavController().navigate(R.id.action_gameFragment_to_gameLostFragment)
            return
        }
        viewModel.spinLuckyWheel()

        //In case of rolling this again
        if (viewModel.wheelResult=="Extra Turn" || viewModel.wheelResult=="Miss Turn"){
            showJokerDialog()
        }
    }

    private fun showExtraTurnDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(viewModel.wheelResult)
            .setMessage("Yay! You rolled \"${viewModel.wheelResult}\" and gained a life")
            .setCancelable(true)
            .show()
        //TODO add skip button og evt gør dynamisk så der ikke er 3 funktioner
    }

    private fun showMissTurnDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(viewModel.wheelResult)
            .setMessage("Dang! You rolled \"${viewModel.wheelResult}\" and lost a life")
            .setCancelable(true)
            .show()
    }

    private fun showBankruptDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(viewModel.wheelResult)
            .setMessage("Dang! You rolled \"${viewModel.wheelResult}\" and lost a your points :(")
            .setCancelable(true)
            .show()
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

    private fun updateLuckyWheelResult(){
        binding.WheelResult.text = viewModel.wheelResult
    }

    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.LetterInput.error = "Oh no! Wrong Guess." // \"${viewModel.lastGuessedChar}\""
        } else {
            binding.textField.isErrorEnabled = false
            binding.LetterInput.text = null
        }
    }
}