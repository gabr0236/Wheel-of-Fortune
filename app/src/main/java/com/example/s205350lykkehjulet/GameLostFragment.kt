package com.example.s205350lykkehjulet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.s205350lykkehjulet.databinding.GameLostFragmentBinding

class GameLostFragment : Fragment() {
    private var _binding: GameLostFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Inflate the layout XML file and return a binding object instance
        val fragmentBinding = GameLostFragmentBinding.inflate(inflater, container, false)
        _binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            //Specify the fragment as the lifecycle owner
            lifecycleOwner = viewLifecycleOwner

            //Assign the view model to a property in the binding class
            gameViewModel = viewModel

            //Assign this fragment
            gameLostFragment = this@GameLostFragment

            finalScore.text = getString(R.string.score, viewModel.score.toString())
            wordReveal.text = getString(R.string.word_reveal, viewModel.currentWordToBeGuessed)
            numberOfGuesses.text = getString(R.string.number_of_guesses, viewModel.numberOfGuesses.toString())
            //Assign navigation to button
            playAgainButton.setOnClickListener {
                findNavController().navigate(R.id.action_gameLostFragment_to_gameFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}