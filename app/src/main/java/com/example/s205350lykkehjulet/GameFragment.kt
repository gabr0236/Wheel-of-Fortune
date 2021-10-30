package com.example.s205350lykkehjulet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
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
        recyclerView.adapter = ItemAdapter(viewModel.letterCardList.value)
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
    fun submitGuess() {
        if (viewModel.gameStage.value==GameStage.IS_GUESS) {
            val playerInputLetter = binding.letterInput.text?.firstOrNull()

            //PlayerInputLetter cannot be null beyond this point
            playerInputLetter ?: return

            binding.letterInput.setText("")

            if (viewModel.isUserInputMatch(playerInputLetter)) {
                if (viewModel.isWon) {
                    findNavController().navigate(R.id.action_gameFragment_to_gameWonFragment)
                }
            } else {
                binding.gameQuote.text=resources.getString(R.string.wrong_guess)
                if (viewModel.lives.value!! <= 0) {
                    findNavController().navigate(R.id.action_gameFragment_to_gameLostFragment)
                }
            }
            updateLetterCards()
            binding.gameQuote.text=resources.getString(R.string.spin_again)
        }
    }

    fun spinWheel(){
        if (viewModel.gameStage.value==GameStage.IS_SPIN) {
            //TODO: sådan nogle calls burde måske ikke ske,
            // måske istedet lav metode der hedder isJoker
            viewModel.spinLuckyWheel()
            if (!viewModel.wheelResult.value?.isDigitsOnly()!!) {
                showJokerDialog()
            }
        }

    }

    private fun updateLetterCards() {
        val posOfLastGuessedChars = viewModel.getPosOfLastGuessedChars()
        for(i in posOfLastGuessedChars.indices)
        recyclerView.adapter?.notifyItemChanged(posOfLastGuessedChars[i])
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
        viewModel.doWheelResultAction()

        //continueGameAfterJokerDialog()
        if (viewModel.lives.value!! <= 0) {
            findNavController().navigate(R.id.action_gameFragment_to_gameLostFragment)
        }
    }

    companion object {
        private const val TAG = "GameFragment"
    }
}