package com.project.projecte_health.presentation.ui.patients

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.project.projecte_health.data.local.dashboard.model.CategoryModel
import com.project.projecte_health.data.local.users.model.UsersModel
import com.project.projecte_health.databinding.FragmentHomeBinding
import com.project.projecte_health.presentation.ui.adapters.CategoryAdapter
import com.project.projecte_health.presentation.ui.adapters.NearbyDoctorsAdapter
import com.project.projecte_health.presentation.ui.registration.LoginActivity
import com.project.projecte_health.utils.Utils
import com.project.projecte_health.utils.Utils.safeNavigate
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var doctorsAdapter: NearbyDoctorsAdapter
    private lateinit var categoriesAdapter: CategoryAdapter

    private var longitude: Double = 0.0
    private var latitude: Double = 0.0

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)


        lifecycleScope.launch {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            checkLocationPermission()
            binding.heading.text =
                "Welcome To HealthConnect,\n" + (activity as DashboardActivity).prefsManager.getUserName()
                    .toString()

        }

        return binding.root
    }

    private fun checkLocationPermission() {
        // Check if the permission is not granted
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        latitude = it.latitude
                        longitude = it.longitude
                        // Use latitude and longitude as needed
                        val databaseReference =
                            (activity as DashboardActivity).database.getReference("users")

                        databaseReference.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    val userList: ArrayList<UsersModel> = ArrayList()

                                    for (userSnapshot in dataSnapshot.children) {
                                        val userId = userSnapshot.key
                                        val userType = userSnapshot.child("userType")
                                            .getValue(String::class.java)
                                        val lat =
                                            userSnapshot.child("lat").getValue(String::class.java)
                                        val lng =
                                            userSnapshot.child("lng").getValue(String::class.java)
                                        val name =
                                            userSnapshot.child("name").getValue(String::class.java)
                                        val speciality = userSnapshot.child("speciality")
                                            .getValue(String::class.java)
                                        val address = userSnapshot.child("address")
                                            .getValue(String::class.java)
                                        val postCode = userSnapshot.child("postCode")
                                            .getValue(String::class.java)

                                        if (userId != null && userType != null) {
                                            // Process the userType for each user
                                            println("User ID: $userId, UserType: $userType")

                                            if (userType.toString() == "Doctor") {
                                                val distance = lat?.toDouble()?.let {
                                                    lng?.toDouble()?.let { it1 ->
                                                        Utils.distanceBetween(
                                                            startLat = latitude,
                                                            startLng = longitude,
                                                            endLat = it,
                                                            endLng = it1
                                                        )
                                                    }
                                                }

                                                if (lat?.isNotEmpty() == true && lng?.isNotEmpty() == true) {
                                                    if (userSnapshot.child("imageUrl").exists()) {
                                                        userList.add(
                                                            UsersModel(
                                                                name.toString(),
                                                                userId,
                                                                distance = distance,
                                                                speciality = speciality.toString(),
                                                                address = address.toString(),
                                                                postCode = postCode.toString(),
                                                                latLng = LatLng(
                                                                    lat.toDouble(),
                                                                    lng.toDouble()
                                                                ),
                                                                imageUrl = userSnapshot.child("imageUrl")
                                                                    .getValue(String::class.java)
                                                            )
                                                        )
                                                    } else {
                                                        userList.add(
                                                            UsersModel(
                                                                name.toString(),
                                                                userId,
                                                                distance = distance,
                                                                speciality = speciality.toString(),
                                                                address = address.toString(),
                                                                postCode = postCode.toString(),
                                                                latLng = LatLng(
                                                                    lat.toDouble(),
                                                                    lng.toDouble()
                                                                )
                                                            )
                                                        )
                                                    }
                                                }
                                            }

                                        }

                                        setNearbyDoctorsAdapter(userList)
                                    }
                                } else {
                                    // The "users" node doesn't exist or is empty
                                    // Handle the case accordingly
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // Handle database error
                            }
                        })
                        println("Latitude: $latitude, Longitude: $longitude")
                    } ?: run {
                        // Handle case where location is null
                    }
                }
                .addOnFailureListener { e ->
                    // Handle failure to get location
                    println("Error getting location: ${e.message}")
                }
        } else {
            // Request permissions
            requestStoragePermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            requestStoragePermissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)

        }
    }


    @SuppressLint("MissingPermission")
    private val requestStoragePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
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
            } else {
            }
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCategoryAdapter()
        actionViews()

    }

    private fun setCategoryAdapter() {

        val userList: ArrayList<CategoryModel> = ArrayList()

        // Adding some user data to the list
        userList.add(CategoryModel(1, "General", 0))
        userList.add(CategoryModel(2, "Dentist", 0))
        userList.add(CategoryModel(3, "Pediatrician", 0))
        userList.add(CategoryModel(4, "Orthodontist", 0))

        categoriesAdapter = CategoryAdapter {
            val action =
                HomeFragmentDirections.actionHomeFragmentToDoctorListingFragment(it.category)
            findNavController().safeNavigate(action)

        }

        categoriesAdapter.submitList(userList)


        binding.categoriesRv.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.categoriesRv.adapter = categoriesAdapter


    }

    private fun actionViews() {
        binding.apply {

            findDoctorsBtn.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToDoctorListingFragment("")
                findNavController().safeNavigate(action)
            }

            logoutBtn.setOnClickListener {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }

        }
    }

    private fun setNearbyDoctorsAdapter(userList: ArrayList<UsersModel>) {

        doctorsAdapter = NearbyDoctorsAdapter(requireContext()) {
            val action = HomeFragmentDirections.actionHomeFragmentToDoctorDetailsFragment(it)
            findNavController().safeNavigate(action)
        }

        doctorsAdapter.submitList(userList)
        binding.nearbyDoctorsRv.adapter = doctorsAdapter
    }
}