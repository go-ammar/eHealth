package com.project.projecte_health.presentation.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.PackageManagerCompat.LOG_TAG
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.project.projecte_health.base.BaseFragment
import com.project.projecte_health.data.local.users.model.ProfileModel
import com.project.projecte_health.databinding.FragmentEditProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class EditProfileFragment : BaseFragment() {

    lateinit var binding: FragmentEditProfileBinding
    lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(layoutInflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch {
            userId = (activity as AccountActivity).prefsManager.getUserId().toString()
            val path = userId
//            database.reference.child("users")
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

        binding.topBar.headingTitle.text = "Edit Profile"

        binding.topBar.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.saveBtn.setOnClickListener {
            val profileData = ProfileModel(
                height = binding.heightEt.text.toString(),
                weight = binding.weightEt.text.toString(),
                bloodType = binding.bloodTypeEt.text.toString(),
                allergies = binding.allergiesEt.text.toString()
            )

            val userData = HashMap<String, Any>()
            userData["height"] = binding.heightEt.text.toString()
            userData["weight"] = binding.weightEt.text.toString()
            userData["bloodType"] = binding.bloodTypeEt.text.toString()
            userData["allergies"] = binding.allergiesEt.text.toString()

            database.reference.child("users").child(userId).updateChildren(userData) { databaseError, _ ->
                    if (databaseError == null) {
                        // Height updated successfully
                        findNavController().popBackStack()
                    } else {
                        // Failed to update height
                    }
                }

//            userRef.child("height").setValue(
//                binding.heightEt.text.toString()
//            )
//            userRef.child("bloodType").setValue(
//                binding.bloodTypeEt.text.toString()
//            )
//            userRef.child("weight").setValue(
//                binding.weightEt.text.toString()
//            )
//            userRef.child("allergies").setValue(
//                binding.allergiesEt.text.toString()
//            )
//            { databaseError, _ ->
//                if (databaseError == null) {
//                    // Height updated successfully
//                    findNavController().popBackStack()
//                } else {
//                    // Failed to update height
//                }
//            }
        }


    }
}