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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.project.projecte_health.base.BaseFragment
import com.project.projecte_health.databinding.FragmentPatientInfoBinding
import com.project.projecte_health.presentation.ui.bottomsheets.BottomSheetSpeciality
import java.util.ArrayList


class PatientInfoFragment : BaseFragment() {

    private var longitude: Double = 0.0
    private var latitude: Double = 0.0
    lateinit var binding: FragmentPatientInfoBinding
    private var specialityList: MutableList<String> = ArrayList()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val args: PatientInfoFragmentArgs by navArgs()

    init {
        specialityList.add("General")
        specialityList.add("Dentist")
        specialityList.add("Pediatrician")
        specialityList.add("Orthodontist")
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPatientInfoBinding.inflate(inflater, container, false)

        if  (args.userType == "Doctor"){
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            getLocation()
            binding.specialityEt.visibility = View.VISIBLE
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

    }
}