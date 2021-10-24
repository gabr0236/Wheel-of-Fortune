package com.example.s205350lykkehjulet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.s205350lykkehjulet.databinding.GameWonFragmentBinding

class GameWonFragment : Fragment() {
    private var binding: GameWonFragmentBinding? = null

    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Inflate the layout XML file and return a binding object instance
        val fragmentBinding = GameWonFragmentBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            //Specify the fragment as the lifecycle owner
            lifecycleOwner = viewLifecycleOwner

            //Assign the view model to a property in the binding class
            gameViewModel = viewModel

            //Assign this fragment
            gameWonFragment = this@GameWonFragment

            //TODO !! -????
            binding!!.finalScore.text = getString(R.string.score, viewModel.score.toString())
            binding!!.livesLeft.text = getString(R.string.lives, viewModel.lives.toString())

            Log.d("GameWonFragment","Score: ${viewModel.score}")
            //Assign navigation to button
            binding!!.playAgainButton.setOnClickListener {
                findNavController().navigate(R.id.action_gameWonFragment_to_gameFragment)
            }

        }
    }
}