package com.example.s205350lykkehjulet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.s205350lykkehjulet.Adapter.ItemAdapter
import com.example.s205350lykkehjulet.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class GameFragment : Fragment() {
    //Recommended way for implementing view binding in fragments
    //Source: https://developer.android.com/topic/libraries/view-binding#fragments
    private var _binding: GameFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "On CreateView")

        //Inflate the layout XML file and return a binding object instance
        _binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)

        //Get random category and word from datasource and set in viewModel
        viewModel.setRandomCategoryAndWord(Datasource(requireContext()).getRandomCategoryAndWord())
        viewModel.newGame()

        //Setup recyclerview
        recyclerView = binding.hiddenWord
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        recyclerView.adapter = ItemAdapter(viewModel.shownWordToBeGuessedAsArray)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            Log.d(TAG, "On ViewCreated")
            //Specify the fragment as the lifecycle owner
            lifecycleOwner = viewLifecycleOwner

            //Assign the view model to a property in the binding class
            gameViewModel = viewModel

            //Assign this fragment
            gameFragment = this@GameFragment
        }
    }

    override fun onDestroyView() {
        Log.d(TAG, "On Destroy")
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        Log.d(TAG, "On Pause")
        super.onPause()
    }

    /**
     * Main function for the game loop
     * Submits the players input and updates the view accordingly
     */
    fun submitGuessAndSpinWheel() {
        val playerInputLetter = binding.letterInput.text?.firstOrNull()

        //PlayerInputLetter cannot be null beyond this point
        playerInputLetter ?: return

        binding.letterInput.setText("")

        if (viewModel.isUserInputMatch(playerInputLetter)) {
            setErrorTextField(false)
            viewModel.spinLuckyWheel()
        } else {
            setErrorTextField(true)
            viewModel.spinLuckyWheel()
        }
        if (!viewModel.wheelResult.value?.isDigitsOnly()!!) {
            showJokerDialog()
        }
        if (viewModel.lives.value!! <= 0) {
            findNavController().navigate(R.id.action_gameFragment_to_gameLostFragment)
        } else if (viewModel.isWon) {
            findNavController().navigate(R.id.action_gameFragment_to_gameWonFragment)
        }
        updateWordToBeGuessedOnScreen()
    }

    /**
     * Display a dialog if player rolled a joker
     */
    private fun showJokerDialog() {
        Log.d(TAG, "showJokerDialog() called")
        val message: String = when (viewModel.wheelResult.value) {
            MISS_TURN -> String.format(resources.getString(R.string.miss_turn_message), MISS_TURN)
            EXTRA_TURN -> String.format(resources.getString(R.string.extra_turn_message), EXTRA_TURN)
            BANKRUPT -> String.format(resources.getString(R.string.bankrupt_message), BANKRUPT)
            else -> throw Exception("WheelResult is not an expected value") //TODO: Det her ok?
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(viewModel.wheelResult.value)
            .setMessage(message)
            .setCancelable(true)
            .show()
        continueGameAfterJokerDialog()
    }

    /**
     * Continuation of the game loop after player rolled a joker
     */
    private fun continueGameAfterJokerDialog() {
        Log.d(TAG, "continueGameAfterJokerDialog() called")

        viewModel.doWheelResultAction()
        if (viewModel.lives.value!! <= 0) { findNavController().navigate(R.id.action_gameFragment_to_gameLostFragment) }
        viewModel.spinLuckyWheel()

        //In case of rolling this again
        if (viewModel.wheelResult.value == EXTRA_TURN
            || viewModel.wheelResult.value == MISS_TURN
            || viewModel.wheelResult.value == BANKRUPT
        ) {
            showJokerDialog()
        }
    }

    private fun updateWordToBeGuessedOnScreen() {
        //TODO det her skal ændres
        recyclerView.adapter = ItemAdapter(viewModel.shownWordToBeGuessedAsArray)
    }

    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.letterInputContainer.isErrorEnabled = true
            binding.letterInput.error = getString(R.string.wrong_guess)
        } else {
            binding.letterInputContainer.isErrorEnabled = false
            binding.letterInput.text = null
        }
    }

    companion object {
        private const val TAG = "GameFragment"
    }
}