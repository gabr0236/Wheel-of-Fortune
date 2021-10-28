package com.example.s205350lykkehjulet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.s205350lykkehjulet.databinding.GameWonFragmentBinding

class GameWonFragment : Fragment() {
    private var _binding: GameWonFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Inflate the layout XML file and return a binding object instance
        val fragmentBinding = GameWonFragmentBinding.inflate(inflater, container, false)
        _binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            gameViewModel = viewModel
            gameWonFragment = this@GameWonFragment

            //TODO: det her kan ligge i xml?
            finalScore.text = getString(R.string.score, viewModel.score.toString())
            livesLeft.text = getString(R.string.lives, viewModel.lives.toString())
            wordReveal.text = getString(R.string.word_reveal, viewModel.currentWordToBeGuessed)
            numberOfGuesses.text = getString(R.string.number_of_guesses, viewModel.numberOfGuesses.toString())

            Log.d(TAG,"Score: ${viewModel.score}")
            //Assign navigation to button
            playAgainButton.setOnClickListener {
                findNavController().navigate(R.id.action_gameWonFragment_to_gameFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    companion object {
        private const val TAG = "GameWonFragment"
    }
}