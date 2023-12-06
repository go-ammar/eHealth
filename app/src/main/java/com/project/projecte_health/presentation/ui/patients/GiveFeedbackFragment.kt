package com.project.projecte_health.presentation.ui.patients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.project.projecte_health.base.BaseFragment
import com.project.projecte_health.databinding.FragmentGiveFeedbackBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class GiveFeedbackFragment : BaseFragment() {

    private lateinit var binding: FragmentGiveFeedbackBinding
    private var userId = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentGiveFeedbackBinding.inflate(layoutInflater, container, false)

        lifecycleScope.launch {
            userId = (activity as AppointmentsActivity).prefsManager.getUserId().toString()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    fun saveFeedback(doctorId: String) {

        val userData = HashMap<String, Any>()
        userData["rating"] = binding.ratingBar.rating
        userData["feedback"] = binding.feedbackEt.text.toString()

        database.reference.child("feedback").child(doctorId).child(userId).setValue(userData)

    }

    fun getFeedback (){
        val doctorId = "yourDoctorId"
        val feedbackReference = database.reference.child("feedback").child(doctorId)

        feedbackReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val userId = userSnapshot.key
                        val rating = userSnapshot.child("rating").getValue(Float::class.java)
                        val feedback = userSnapshot.child("feedback").getValue(String::class.java)

                        // Use the retrieved data (userId, rating, and feedback)
                        if (userId != null && rating != null && feedback != null) {
                            println("User ID: $userId, Rating: $rating, Feedback: $feedback")
                        }
                    }
                } else {
                    println("No feedback found for the specified doctor.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                println("Error retrieving feedback data: ${error.message}")
            }
        })
    }
}