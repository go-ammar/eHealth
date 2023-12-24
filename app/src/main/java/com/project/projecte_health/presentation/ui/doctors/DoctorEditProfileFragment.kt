package com.project.projecte_health.presentation.ui.doctors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.project.projecte_health.base.BaseFragment
import com.project.projecte_health.data.local.users.model.Availability
import com.project.projecte_health.data.local.users.model.ProfileModel
import com.project.projecte_health.databinding.FragmentDoctorEditProfileBinding
import com.project.projecte_health.presentation.ui.bottomsheets.BottomSheetSpeciality
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DoctorEditProfileFragment : BaseFragment() {

    private var daysList: MutableList<String> = ArrayList()
    private lateinit var binding: FragmentDoctorEditProfileBinding
    lateinit var userId: String

    private val timeList = listOf(
        "09:00", "10:00", "11:00", "12:00",
        "13:00", "14:00", "15:00", "16:00",
        "17:00", "18:00"
    )

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
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDoctorEditProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            userId = (activity as DocAccountActivity).prefsManager.getUserId().toString()
            val userRef = database.reference.child("users/$userId")

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Access the value from the DataSnapshot
                        val profileData = snapshot.getValue(ProfileModel::class.java)
                        // Do something with the height
                        if (profileData != null) {
                            // Process the height value
                            binding.profileData = profileData
                            println("Height: $profileData")
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

        binding.saveBtn.setOnClickListener {

            val userData = HashMap<String, Any>()

            val daysCheckboxes = listOf(
                binding.checkboxMonday,
                binding.checkboxTuesday,
                binding.checkboxWednesday,
                binding.checkboxThursday,
                binding.checkboxFriday,
                binding.checkboxSaturday,
                binding.checkboxSunday
            )

            val availability = Availability(
                days = getSelectedDays(daysCheckboxes),
                startTime = binding.startTimeEt.text.toString(),
                endTime = binding.endTimeEt.text.toString()
            )

            userData["availability"] = availability


            database.reference.child("users").child(userId)
                .updateChildren(userData) { databaseError, _ ->
                    if (databaseError == null) {
                        // Height updated successfully
                    } else {
                        // Failed to update height
                    }
                }

        }


        binding.startTimeEt.setOnClickListener {

            val btmSheetDialogFragment = BottomSheetSpeciality(
                timeList
            )
            btmSheetDialogFragment.show(
                childFragmentManager,
                BottomSheetSpeciality.TAG
            )

            btmSheetDialogFragment.stringData.observe(viewLifecycleOwner) {
                binding.startTimeEt.text = it
                binding.endTimeEt.visibility = View.VISIBLE
                binding.endTimedescTv.visibility = View.VISIBLE
            }
        }

        binding.endTimeEt.setOnClickListener {

            val btmSheetDialogFragment = BottomSheetSpeciality(
                timeList
            )
            btmSheetDialogFragment.show(
                childFragmentManager,
                BottomSheetSpeciality.TAG
            )

            btmSheetDialogFragment.stringData.observe(viewLifecycleOwner) {
                if (binding.startTimeEt.text.toString().substring(0, 2).toInt() < it.substring(0, 2)
                        .toInt()
                ) {
                    binding.endTimeEt.text = it
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Choose a time after starting Time",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }

    }

    private fun getSelectedDays(daysCheckboxes: List<CheckBox>): List<String> {
        val selectedDays = mutableListOf<String>()

        for ((index, checkbox) in daysCheckboxes.withIndex()) {
            if (checkbox.isChecked) {
                // Add the day based on the index (assuming daysCheckboxes is ordered correctly)
                selectedDays.add(getDayFromIndex(index))
            }
        }

        return selectedDays
    }

    private fun getDayFromIndex(index: Int): String {
        return when (index) {
            0 -> "Monday"
            1 -> "Tuesday"
            2 -> "Wednesday"
            3 -> "Thursday"
            4 -> "Friday"
            5 -> "Saturday"
            6 -> "Sunday"
            else -> throw IllegalArgumentException("Invalid index")
        }
    }


}
