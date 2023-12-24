package com.project.projecte_health.presentation.ui.doctors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.project.projecte_health.base.BaseFragment
import com.project.projecte_health.data.local.users.model.ProfileModel
import com.project.projecte_health.databinding.FragmentDoctorProfileBinding
import com.project.projecte_health.presentation.ui.bottomsheets.BottomSheetSpeciality
import com.project.projecte_health.utils.Utils.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.ArrayList


@AndroidEntryPoint
class DoctorProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentDoctorProfileBinding
    lateinit var userId: String
    private var daysList: MutableList<String> = ArrayList()

    init {
        daysList.add("Monday")
        daysList.add("Tuesday")
        daysList.add("Wednesday")
        daysList.add("Thursday")
        daysList.add("Friday")
        daysList.add("Saturday")
        daysList.add("Sunday")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentDoctorProfileBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment

        lifecycleScope.launch {
            userId = (activity as DocAccountActivity).prefsManager.getUserId().toString()
            val userRef = database.reference.child("users/$userId")

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Access the value from the DataSnapshot
                        val profileData = snapshot.getValue(ProfileModel::class.java)
                        if (profileData != null) {

                            binding.tvProfileName.text = profileData.name

                            requireActivity().runOnUiThread {
                                // Your UI update code here
                                if (profileData.imageUrl?.isNotEmpty() == true)
                                    Glide.with(requireContext())
                                        .load(profileData.imageUrl)
                                        .into(binding.ivProfile)
                            }
                        }
                    } else {
                        // Data does not exist
                        println("Data does not exist")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle potential errors
                    println("Database error: ${error.message}")
                }
            })
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.editBtn.setOnClickListener {
            val action = DoctorProfileFragmentDirections.actionDoctorProfileFragmentToDoctorEditProfileFragment()
            findNavController().safeNavigate(action)
        }

    }
}