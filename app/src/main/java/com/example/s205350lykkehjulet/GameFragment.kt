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
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.example.s205350lykkehjulet.Adapter.ItemAdapter
import com.example.s205350lykkehjulet.databinding.GameFragmentBinding
import com.google.android.flexbox.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class GameFragment : Fragment() {
    //Recommended way for implementing view binding in fragments
    //Source: https://developer.android.com/topic/libraries/view-binding#fragments
    private var _binding: GameFragmentBinding? = null
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
        _binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)

        //Get random category and word from datasource and set in viewModel
        viewModel.setCategoryAndCurrentWordToBeGuessed(Datasource(requireContext()).getRandomCategoryAndWord())
        viewModel.newGame()
        viewModel.setGameQuote(getString(R.string.initial_game_quote))

        //Bind to ButterKnife (For animated wheel spin)
        activity?.let { ButterKnife.bind(it) }
        luckyWheel = LuckyWheel(binding.luckyWheel, this)

        //Setup recyclerview
        recyclerView = binding.letterCardRecyclerview

        //TODO flexbox atributes in xml
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.justifyContent = JustifyContent.CENTER
        layoutManager.alignItems = AlignItems.CENTER
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.flexWrap = FlexWrap.WRAP
        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = viewModel.letterCardList.value?.let { ItemAdapter(it) }


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
        if (viewModel.gameStage.value == GameStage.GUESS) {
            val playerInputLetter = binding.letterInput.text?.firstOrNull()

            //PlayerInputLetter cannot be null beyond this point
            playerInputLetter ?: return

            binding.letterInput.setText("")

            if (viewModel.isUserInputMatch(playerInputLetter)) {
                viewModel.setGameQuote(getString(R.string.correct_guess_game_quote))
                if (viewModel.gameStage.value==GameStage.GAME_WON) {
                    findNavController().navigate(R.id.action_gameFragment_to_gameWonFragment)
                }
            } else {
                viewModel.setGameQuote(getString(R.string.wrong_guess_game_quote))
                if (viewModel.gameStage.value==GameStage.GAME_LOST) {
                    findNavController().navigate(R.id.action_gameFragment_to_gameLostFragment)
                }
            }
            updateLetterCards()
        }
    }

    fun spinWheel() {
        if (viewModel.gameStage.value == GameStage.SPIN) {
            //The gameStage needs to be set to WAITING to prevent the
                // user from respinning or guessing while wheel is turning
                    viewModel.setGameStage(GameStage.WAITING)
            luckyWheel?.spinWheelImage()
        }
    }

    fun continueGameAfterWheelSpin(wheelResult: String) {
        viewModel.setGameStage(GameStage.GUESS)
        viewModel.setWheelResult(wheelResult)
        if (!wheelResult.isDigitsOnly()) {
            showJokerDialog()
        } else {
            viewModel.setGameQuote(
                String.format(
                    getString(R.string.is_guess_game_quote),
                    viewModel.wheelResult.value
                )
            )
        }
    }

    private fun updateLetterCards() {
        val posOfLastGuessedChars = viewModel.getPosOfLastGuessedChars()
        for (i in posOfLastGuessedChars.indices)
            recyclerView.adapter?.notifyItemChanged(posOfLastGuessedChars[i])
    }

    /**
     * Display a dialog if player rolled a joker
     */
    private fun showJokerDialog() {
        Log.d(TAG, "showJokerDialog() called")
        val message: String = when (viewModel.wheelResult.value) {
            MISS_TURN -> String.format(resources.getString(R.string.miss_turn_message), MISS_TURN)
            EXTRA_TURN -> String.format(
                resources.getString(R.string.extra_turn_message),
                EXTRA_TURN
            )
            BANKRUPT -> String.format(resources.getString(R.string.bankrupt_message), BANKRUPT)
            else -> ""
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(viewModel.wheelResult.value)
            .setMessage(message)
            .setCancelable(true)
            .show()
        viewModel.setGameQuote(
            String.format(
                getString(R.string.spin_again_game_quote),
                viewModel.wheelResult.value
            )
        )
        viewModel.doWheelResultAction()

        //continueGameAfterJokerDialog()
        if (viewModel.gameStage.value==GameStage.GAME_LOST) {
            findNavController().navigate(R.id.action_gameFragment_to_gameLostFragment)
        }
    }

    companion object {
        private const val TAG = "GameFragment"
    }
}