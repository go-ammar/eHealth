package com.project.projecte_health.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.project.projecte_health.R
import com.project.projecte_health.data.local.users.model.DoctorDetailModel
import com.project.projecte_health.data.local.users.model.UsersModel
import com.project.projecte_health.databinding.FragmentDoctorListingBinding
import com.project.projecte_health.databinding.FragmentEditProfileBinding
import com.project.projecte_health.presentation.ui.adapters.DoctorListAdapter
import com.project.projecte_health.utils.Utils
import com.project.projecte_health.utils.Utils.safeNavigate


class DoctorListingFragment : Fragment() {

    lateinit var binding: FragmentDoctorListingBinding
    private val args: DoctorListingFragmentArgs by navArgs()
    lateinit var adapter: DoctorListAdapter

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

                                if (args.speciality.toString() == "") {

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
                                                )
                                            )
                                        )
                                    }
                                } else if (args.speciality.toString() == speciality) {
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
                                                )
                                            )
                                        )
                                    }
                                }
                            }

                        }

                        setAdapter(userList)
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