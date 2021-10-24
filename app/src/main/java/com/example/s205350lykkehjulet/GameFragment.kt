package com.example.s205350lykkehjulet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.s205350lykkehjulet.Adapter.ItemAdapter
import com.example.s205350lykkehjulet.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class GameFragment : Fragment() {
    private var binding: GameFragmentBinding? = null
    private lateinit var recyclerView: RecyclerView

    private val viewModel: GameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("GameFragment", "On CreateView")

        //Inflate the layout XML file and return a binding object instance
        val fragmentBinding = GameFragmentBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        //Get random category and word from datasouce and set in viewModel
        viewModel.setRandomCategoryAndWord(Datasource(requireContext()).getRandomCategoryAndWord())
        viewModel.newGame()

        //Setup recyclerview
        recyclerView = fragmentBinding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        recyclerView.adapter = ItemAdapter(viewModel.shownWordToBeGuessedAsArray)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            Log.d("GameFragment", "On ViewCreated")
            //Specify the fragment as the lifecycle owner
            lifecycleOwner = viewLifecycleOwner

            //Assign the view model to a property in the binding class
            gameViewModel = viewModel

            //Assign this fragment
            gameFragment = this@GameFragment

            //Setup a click listener for the Submit
            GuessButton.setOnClickListener { submitGuessAndSpinWheel() }
            updateGameQuote()
            updateCategory()
        }
    }

    override fun onDestroyView() {
        Log.d("GameFragment", "On Destroy")
        super.onDestroyView()
        binding=null
    }

    override fun onPause() {
        Log.d("GameFragment", "On Pause")

        super.onPause()
    }

    /**
     * Main function for the game loop
     * Submits the players input and updates the view accordingly
     */
    private fun submitGuessAndSpinWheel() {
        val playerInputLetter = binding?.LetterInput?.text?.firstOrNull()

        //PlayerInputLetter cannot be null beyond this point
        playerInputLetter ?: return

        //Resets LetterInput field
        //TODO: det her ok?
        binding?.LetterInput?.setText("")

        if (viewModel.isUserInputMatch(playerInputLetter)) {
            viewModel.doWheelResultAction()
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

    /**
     * For a complete GUI update, except from category
     */
    private fun updateView() {
        updateLives()
        updateLuckyWheelResult()
        updateScore()
        updateGameQuote()
        updateWordToBeGuessedOnScreen()
    }

    /**
     * Updates the game quote
     */
    private fun updateGameQuote() {
        binding?.GameQuote?.text =
            String.format(resources.getString(R.string.game_quote), viewModel.wheelResult)
    }

    /**
     * Display a dialog if player rolled a joker
     */
    private fun showJokerDialog() {
        Log.d("GameFragment", "showJokerDialog() called")

        val message: String = when (viewModel.wheelResult) {
            "Miss Turn" -> "Dang! You rolled \"${viewModel.wheelResult}\" and lost a life"
            "Extra Turn" -> "Yay! You rolled \"${viewModel.wheelResult}\" and gained a life"
            "Bankrupt" -> "Dang! You rolled \"${viewModel.wheelResult}\" and lost a your points :("
            else -> throw Exception("WheelResult is not an expected value") //TODO: Det her ok?
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(viewModel.wheelResult)
            .setMessage(message)
            .setCancelable(true)
            .show()
        continueGameAfterJokerDialog()
    }

    /**
     * Continuation of the game loop after player rolled a joker
     */
    private fun continueGameAfterJokerDialog() {
        Log.d("GameFragment", "continueGameAfterJokerDialog() called")

        viewModel.doWheelResultAction()
        if (viewModel.lives <= 0) {
            findNavController().navigate(R.id.action_gameFragment_to_gameLostFragment)
            return
        }
        viewModel.spinLuckyWheel()

        //In case of rolling this again
        if (viewModel.wheelResult == "Extra Turn"
            || viewModel.wheelResult == "Miss Turn"
            || viewModel.wheelResult == "Bankrupt"
        ) {
            showJokerDialog()
        }
    }

    private fun updateLives() {
        binding?.Lives?.text = getString(R.string.lives, viewModel.lives.toString())
    }

    private fun updateScore() {
        binding?.Score?.text = getString(R.string.score, viewModel.score.toString())
    }

    private fun updateWordToBeGuessedOnScreen(){
        //TODO det her skal ændres
        recyclerView.adapter = ItemAdapter(viewModel.shownWordToBeGuessedAsArray)
    }

    private fun updateLuckyWheelResult() {
        binding?.WheelResult?.text = viewModel.wheelResult
    }

    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding?.textField?.isErrorEnabled = true
            binding?.LetterInput?.error = "Oh no! Wrong Guess."
        } else {
            binding?.textField?.isErrorEnabled = false
            binding?.LetterInput?.text = null
        }
    }

    private fun updateCategory() {
        binding?.Catagory?.text =
            String.format(resources.getString(R.string.category), viewModel.category)
    }

}