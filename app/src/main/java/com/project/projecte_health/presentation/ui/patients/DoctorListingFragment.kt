package com.project.projecte_health.presentation.ui.patients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.project.projecte_health.base.BaseFragment
import com.project.projecte_health.data.local.users.UsersModel
import com.project.projecte_health.databinding.FragmentDoctorListingBinding
import com.project.projecte_health.presentation.ui.adapters.DoctorListAdapter
import com.project.projecte_health.utils.Utils.safeNavigate


class DoctorListingFragment : BaseFragment() {

    private lateinit var binding: FragmentDoctorListingBinding
    private val args: DoctorListingFragmentArgs by navArgs()
    private lateinit var adapter: DoctorListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDoctorListingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val databaseReference =
            database.getReference("users")

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

                                if (args.speciality.toString() == "") {

                                    database.getReference("feedback").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            var totalRating = 0.0
                                            var feedbackCount = 0

                                            // Iterate through feedback entries for the specified doctor
                                            for (feedbackSnapshot in dataSnapshot.children) {
                                                val rating = feedbackSnapshot.child("rating").getValue(Int::class.java)

                                                // Check if the "rating" field exists and is a valid number
                                                if (rating != null) {
                                                    totalRating += rating
                                                    feedbackCount++
                                                }
                                            }

                                            if (feedbackCount > 0) {
                                                if (lat?.isNotEmpty() == true && lng?.isNotEmpty() == true) {
                                                    val averageRating = totalRating / feedbackCount
                                                    userList.add(
                                                        UsersModel(
                                                            name = name.toString(),
                                                            userId = userId,
                                                            speciality = speciality.toString(),
                                                            address = address.toString(),
                                                            postCode = postCode.toString(),
                                                            latLng = LatLng(
                                                                lat.toDouble(),
                                                                lng.toDouble()
                                                            ),
                                                            rating = averageRating
                                                        )
                                                    )
                                                }
                                                // Now, you have the averageRating for the specified doctor
                                                // You can use it as needed, for example, display it or perform further actions
//                                                println("Average Rating for $doctorIdToQuery: $averageRating")
                                            } else {
                                                if (lat?.isNotEmpty() == true && lng?.isNotEmpty() == true) {
                                                    userList.add(
                                                        UsersModel(
                                                            name = name.toString(),
                                                            userId = userId,
                                                            speciality = speciality.toString(),
                                                            address = address.toString(),
                                                            postCode = postCode.toString(),
                                                            latLng = LatLng(
                                                                lat.toDouble(),
                                                                lng.toDouble()
                                                            ),
                                                            rating = 5.0
                                                        )
                                                    )
                                                }
                                                // Handle the case when there are no feedback entries
//                                                println("No feedback available for $doctorIdToQuery")
                                            }
                                            setAdapter(userList)


                                        }

                                        override fun onCancelled(databaseError: DatabaseError) {
                                            // Handle errors
                                            println("Error fetching feedback: $databaseError")
                                        }
                                    })


                                } else if (args.speciality.toString() == speciality) {
                                    if (lat?.isNotEmpty() == true && lng?.isNotEmpty() == true) {
                                        database.getReference("feedback").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                var totalRating = 0.0
                                                var feedbackCount = 0

                                                // Iterate through feedback entries for the specified doctor
                                                for (feedbackSnapshot in dataSnapshot.children) {
                                                    val rating = feedbackSnapshot.child("rating").getValue(Int::class.java)

                                                    // Check if the "rating" field exists and is a valid number
                                                    if (rating != null) {
                                                        totalRating += rating
                                                        feedbackCount++
                                                    }
                                                }

                                                if (feedbackCount > 0) {
                                                    if (lat.isNotEmpty() && lng.isNotEmpty()) {
                                                        val averageRating = totalRating / feedbackCount
                                                        userList.add(

                                                            UsersModel(
                                                                name = name.toString(),
                                                                userId = userId,
                                                                speciality = speciality.toString(),
                                                                address = address.toString(),
                                                                postCode = postCode.toString(),
                                                                latLng = LatLng(
                                                                    lat.toDouble(),
                                                                    lng.toDouble()
                                                                ),
                                                                rating = averageRating
                                                            )
                                                        )
                                                    }
                                                    // Now, you have the averageRating for the specified doctor
                                                    // You can use it as needed, for example, display it or perform further actions
//                                                println("Average Rating for $doctorIdToQuery: $averageRating")
                                                } else {
                                                    if (lat.isNotEmpty() && lng.isNotEmpty()) {
                                                        userList.add(
                                                            UsersModel(
                                                                name = name.toString(),
                                                                userId = userId,
                                                                speciality = speciality.toString(),
                                                                address = address.toString(),
                                                                postCode = postCode.toString(),
                                                                latLng = LatLng(
                                                                    lat.toDouble(),
                                                                    lng.toDouble()
                                                                ),
                                                                rating = 5.0
                                                            )
                                                        )
                                                    }
                                                    // Handle the case when there are no feedback entries
//                                                println("No feedback available for $doctorIdToQuery")
                                                }
                                                setAdapter(userList)


                                            }

                                            override fun onCancelled(databaseError: DatabaseError) {
                                                // Handle errors
                                                println("Error fetching feedback: $databaseError")
                                            }
                                        })

                                    }
                                }
                            }

                        }

//                        setAdapter(userList)
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


        binding.heading.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.heading.headingTitle.text = "Doctors List"



    }

    private fun setAdapter(doctorList: java.util.ArrayList<UsersModel>) {

        adapter = DoctorListAdapter {

            val action =
                DoctorListingFragmentDirections.actionDoctorListingFragmentToDoctorDetailsFragment(
                    it
                )
            findNavController().safeNavigate(action)


        }

        binding.doctorRv.adapter = adapter

        adapter.submitList(doctorList)

    }


}