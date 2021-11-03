package com.example.s205350lykkehjulet.fragments

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
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.example.s205350lykkehjulet.*
import com.example.s205350lykkehjulet.adapter.LetterCardAdapter
import com.example.s205350lykkehjulet.databinding.FragmentGameBinding
import com.google.android.flexbox.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.s205350lykkehjulet.R


/**
 * Main fragment responsible for letting the game play
 */
class GameFragment : Fragment() {
    //Recommended way for implementing view binding in fragments
    //Source: https://developer.android.com/topic/libraries/view-binding#fragments
    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private val viewModel: GameViewModel by activityViewModels()

    private var luckyWheel: LuckyWheel? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "On CreateView")

        //Inflate the layout XML file and return a binding object instance
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)

        //Get random category and word from datasource and set in viewModel
        viewModel.setCategoryAndCurrentWordToBeGuessed(Datasource(requireContext()).getRandomCategoryAndWord())
        viewModel.newGame()
        viewModel.setGameQuote(getString(R.string.initial_game_quote))

        //Bind to ButterKnife (For animated wheel spin)
        activity?.let { ButterKnife.bind(it) }
        luckyWheel = LuckyWheel(binding.imageLuckywheel, this)

        //Setup recyclerview
        recyclerView = binding.recyclerviewLetterCards

        //TODO flexbox attributes in xml
        //Setup FlexboxLayout for auto fitting LetterCards in the recyclerview
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.justifyContent = JustifyContent.CENTER
        layoutManager.alignItems = AlignItems.CENTER
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.flexWrap = FlexWrap.WRAP
        recyclerView.layoutManager = layoutManager

        //Pass LetterCards onto adapter
        recyclerView.adapter = viewModel.letterCardList.value?.let { LetterCardAdapter(it) }


        //val letterCardView = (recyclerView.layoutManager as FlexboxLayoutManager).findViewByPosition(2)
        //val lp = letterCardView?.layoutParams as? FlexboxLayout.LayoutParams
        //lp?.isWrapBefore=true
        //letterCardView?.layoutParams=lp

        return binding.root
    }


    private fun letterCardsColumns(): Int {
        val MAX_NUMBER_OF_COLUMNS = 11
        val letterCardList = viewModel.letterCardList.value!!
        return if (letterCardList.size <= MAX_NUMBER_OF_COLUMNS) MAX_NUMBER_OF_COLUMNS
        else {
            var lastSpaceBeforeMaxColumns: Int? = null
            for (i in 0 until MAX_NUMBER_OF_COLUMNS) {
                if (letterCardList[i].letter == ' ') {
                    lastSpaceBeforeMaxColumns = i
                }
            }
            if (lastSpaceBeforeMaxColumns != null) {
                letterCardList.removeAt(lastSpaceBeforeMaxColumns)
            }
            lastSpaceBeforeMaxColumns ?: MAX_NUMBER_OF_COLUMNS
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            Log.d(TAG, "On ViewCreated")
            lifecycleOwner = viewLifecycleOwner //Specify the fragment as the lifecycle owner
            gameViewModel = viewModel //Assign the view model to a property in the binding class
            gameFragment = this@GameFragment //Assign this fragment
        }
    }

    override fun onDestroyView() {
        Log.d(TAG, "On Destroy")
        super.onDestroyView()
        _binding = null
    }

    /**
     * Called when button_guess is clicked.
     *
     * Passes player input onto the viewModel through isUserInputMatch()
     * Updates the gameQuote.
     * Navigates to fragment_game_won or fragment_game_lost
     * if the gameStage is either.
     * Lastly calls updateLetterCards()
     */
    fun submitGuess() {
        if (viewModel.gameStage.value == GameStage.GUESS) {
            val playerInputLetter = binding.editTextLetter.text?.firstOrNull()

            //PlayerInputLetter cannot be null beyond this point
            playerInputLetter ?: return

            binding.editTextLetter.setText("")

            if (viewModel.isUserInputMatch(playerInputLetter)) {
                viewModel.setGameQuote(getString(R.string.correct_guess_game_quote))
                if (viewModel.gameStage.value == GameStage.GAME_WON) {
                    findNavController().navigate(R.id.action_gameFragment_to_gameWonFragment)
                }
            } else {
                viewModel.setGameQuote(getString(R.string.wrong_guess_game_quote))
                if (viewModel.gameStage.value == GameStage.GAME_LOST) {
                    findNavController().navigate(R.id.action_gameFragment_to_gameLostFragment)
                }
            }
            updateLetterCards(viewModel.getPosOfLastGuessedChars())
        }
    }

    /**
     * Calls luckyWheel.spinWheelImage()
     */
    fun spinWheel() {
        if (viewModel.gameStage.value == GameStage.SPIN) {
            //The gameStage needs to be set to WAITING to prevent the
            //user from re-spinning or guessing while wheel is turning
            viewModel.setGameStage(GameStage.WAITING)
            luckyWheel?.spinWheelImage()
        }
    }

    /**
     * Function for continuing game after wheel spin.
     *
     * If wheelResult is not digits, call showJokerDialog() and doWheelResultAction()
     * else set gameStage to GUESS
     *
     * @param wheelResult result of wheel spin
     */
    fun continueGameAfterWheelSpin(wheelResult: String) {
        viewModel.setWheelResult(wheelResult)
        if (!wheelResult.isDigitsOnly()) {
            showJokerDialog()
            viewModel.setGameQuote(
                String.format(
                    getString(R.string.spin_again_game_quote),
                    viewModel.wheelResult.value
                )
            )
            viewModel.doWheelResultAction()
            if (viewModel.gameStage.value == GameStage.GAME_LOST) {
                findNavController().navigate(R.id.action_gameFragment_to_gameLostFragment)
            }
        } else {
            viewModel.setGameStage(GameStage.GUESS)
            viewModel.setGameQuote(
                String.format(
                    getString(R.string.is_guess_game_quote),
                    viewModel.wheelResult.value
                )
            )
        }
    }

    /**
     * Update LetterCards of the last guessed character(s)
     * in recyclerview adapter
     *
     * @param posOfLastGuessedChars index of last guessed char
     */
    private fun updateLetterCards(posOfLastGuessedChars: List<Int>) {
        for (i in posOfLastGuessedChars.indices)
            recyclerView.adapter?.notifyItemChanged(posOfLastGuessedChars[i])
    }

    /**
     * Display a dialog if player rolled a joker (Extra turn, Miss turn or Bankrupt)
     */
    private fun showJokerDialog() {
        if (viewModel.wheelResult.value?.isDigitsOnly() == false) {
            Log.d(TAG, "showJokerDialog() called")
            //Construct message depending on joker value (Extra turn, Miss turn or Bankrupt)
            val message: String = when (viewModel.wheelResult.value) {
                MISS_TURN -> String.format(
                    resources.getString(R.string.miss_turn_message),
                    MISS_TURN
                )
                EXTRA_TURN -> String.format(
                    resources.getString(R.string.extra_turn_message),
                    EXTRA_TURN
                )
                BANKRUPT -> String.format(resources.getString(R.string.bankrupt_message), BANKRUPT)
                else -> return
            }
            //Display constructed message
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(viewModel.wheelResult.value)
                .setMessage(message)
                .setCancelable(true)
                .show()
        }
    }

    companion object {
        private const val TAG = "GameFragment"
    }
}