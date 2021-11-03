package com.example.s205350lykkehjulet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.s205350lykkehjulet.GameViewModel
import com.example.s205350lykkehjulet.R
import com.example.s205350lykkehjulet.databinding.FragmentGameLostBinding

class GameLostFragment : Fragment() {
    private var _binding: FragmentGameLostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Inflate the layout XML file and return a binding object instance
        val fragmentBinding = FragmentGameLostBinding.inflate(inflater, container, false)
        _binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            gameViewModel = viewModel
            gameLostFragment = this@GameLostFragment
        }
    }

    fun playAgain(){
        findNavController().navigate(R.id.action_gameLostFragment_to_gameFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}