package com.example.s205350lykkehjulet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.s205350lykkehjulet.databinding.GameLostFragmentBinding

class GameLostFragment : Fragment() {
    private var binding: GameLostFragmentBinding? = null

    private val viewModel: GameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout XML file and return a binding object instance
        val fragmentBinding = GameLostFragmentBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            // Specify the fragment as the lifecycle owner
            lifecycleOwner = viewLifecycleOwner

            // Assign the view model to a property in the binding class
            binding!!.gameViewModel = viewModel

            // Assign the fragment
            gameLostFragment = this@GameLostFragment

            binding!!.playAgainButton.setOnClickListener {
                findNavController().navigate(R.id.action_gameLostFragment_to_gameFragment)
            }
        }
    }
}