package com.project.projecte_health.presentation.ui.registration

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.project.projecte_health.base.BaseFragment
import com.project.projecte_health.data.local.users.model.Availability
import com.project.projecte_health.databinding.FragmentPatientInfoBinding
import com.project.projecte_health.presentation.ui.bottomsheets.BottomSheetSpeciality
import java.util.ArrayList


class PatientInfoFragment : BaseFragment() {

    private var longitude: Double = 0.0
    private var latitude: Double = 0.0
    lateinit var binding: FragmentPatientInfoBinding
    private var specialityList: MutableList<String> = ArrayList()
    private var daysList: MutableList<String> = ArrayList()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val args: PatientInfoFragmentArgs by navArgs()

    private val timeList = listOf(
        "09:00", "10:00", "11:00", "12:00",
        "13:00", "14:00", "15:00", "16:00",
        "17:00", "18:00"
    )


    init {
        specialityList.add("General")
        specialityList.add("Dentist")
        specialityList.add("Pediatrician")
        specialityList.add("Orthodontist")

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
        binding = FragmentPatientInfoBinding.inflate(inflater, container, false)

        if (args.userType == "Doctor") {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            getLocation()
            binding.specialityEt.visibility = View.VISIBLE

            binding.daysTv.visibility = View.VISIBLE
            binding.checkboxMonday.visibility = View.VISIBLE
            binding.checkboxTuesday.visibility = View.VISIBLE
            binding.checkboxWednesday.visibility = View.VISIBLE
            binding.checkboxThursday.visibility = View.VISIBLE
            binding.checkboxFriday.visibility = View.VISIBLE
            binding.checkboxSaturday.visibility = View.VISIBLE
            binding.checkboxSunday.visibility = View.VISIBLE
            binding.startTimeTv.visibility = View.VISIBLE
            binding.startTimeEt.visibility = View.VISIBLE

        }
        return binding.root

    }

    private val requestStoragePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
//                imagePickerIntent()
            } else {
//                checkPermissions()
            }
        }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestStoragePermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            requestStoragePermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    latitude = it.latitude
                    longitude = it.longitude
                    // Use latitude and longitude as needed
                    println("Latitude: $latitude, Longitude: $longitude")
                } ?: run {
                    // Handle case where location is null
                }
            }
            .addOnFailureListener { e ->
                // Handle failure to get location
                println("Error getting location: ${e.message}")
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginBtn.setOnClickListener {


            auth.createUserWithEmailAndPassword(args.email, args.password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Registration success
                        Log.d("SignUpActivity", "createUserWithEmail:success")
                        val user = auth.currentUser

                        // Store additional details in Firebase Realtime Database
                        user?.let {
                            val userId = it.uid
                            val userReference = database.reference.child("users").child(userId)

                            // Customize the data structure based on your needs
                            val userData = HashMap<String, Any>()
                            userData["name"] = binding.nameEt.text.toString()
                            userData["phoneNumber"] = binding.phoneNumberEt.text.toString()
                            userData["address"] = binding.addressEt.text.toString()
                            userData["postCode"] = binding.postCodeEt.text.toString()
                            userData["userType"] = args.userType

                            if (args.userType == "Doctor") {
                                userData["lat"] = latitude.toString()
                                userData["lng"] = longitude.toString()
                                userData["speciality"] = binding.specialityEt.text.toString()

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
                                    endTime = binding.endTimeTv.text.toString()
                                )
                                userData["availability"] = availability

                            }

                            userReference.setValue(userData)
                        }

                        val intent = Intent(requireActivity(), LoginActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                        // You can navigate to another activity or perform other actions here
                    } else {
                        // Registration failed
                        Log.w("SignUpActivity", "createUserWithEmail:failure", task.exception)
                        // Handle errors, display a message, etc.
                    }
                }

        }

        binding.specialityEt.setOnClickListener {

            val btmSheetDialogFragment = BottomSheetSpeciality(
                specialityList
            )
            btmSheetDialogFragment.show(
                childFragmentManager,
                BottomSheetSpeciality.TAG
            )

            btmSheetDialogFragment.stringData.observe(viewLifecycleOwner) {
                binding.specialityEt.text = it
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
                binding.endTimeTv.visibility = View.VISIBLE
                binding.endTimedescTv.visibility = View.VISIBLE
            }
        }

        binding.endTimeTv.setOnClickListener {

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
                    binding.endTimeTv.text = it
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
