package com.project.projecte_health.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.project.projecte_health.R
import com.project.projecte_health.databinding.FragmentProfileBinding
import com.project.projecte_health.utils.Utils.safeNavigate


class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editBtn.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment()
            findNavController().safeNavigate(action)
        }

    }
}