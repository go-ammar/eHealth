package com.project.projecte_health.presentation.ui.patients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.project.projecte_health.base.BaseFragment
import com.project.projecte_health.data.local.bookings.Appointment
import com.project.projecte_health.databinding.FragmentGiveFeedbackBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class GiveFeedbackFragment : BaseFragment() {

    private lateinit var binding: FragmentGiveFeedbackBinding
    private var userId = ""

    private val args: GiveFeedbackFragmentArgs by navArgs()

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


        binding.sendFeedbackBtn.setOnClickListener {
            saveFeedback(args.appointment.doctorId.toString())
        }

    }

    fun saveFeedback(doctorId: String) {

        val feedbackData = HashMap<String, Any>()
        feedbackData["rating"] = binding.ratingBar.rating
        feedbackData["feedback"] = binding.feedbackEt.text.toString()
        feedbackData["patientId"] = userId
        database.reference.child("feedback").child(doctorId).push().setValue(feedbackData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Successfully saved feedback


                    val appointmentsQuery: Query = args.appointment.date?.toDouble()?.let {
                        database.reference.child("appointments")
//                            .orderByChild("patientId")
//                            .equalTo(userId)
//                            .orderByChild("doctorId").equalTo(args.appointment.doctorId)
                            .orderByChild("date").equalTo(it)
//                            .orderByChild("endTime")
//                            .equalTo(args.appointment.endTime)
                    }!!

                    appointmentsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (appointmentSnapshot in dataSnapshot.children) {
                                val appointment =
                                    appointmentSnapshot.getValue(Appointment::class.java)

                                if (appointment != null && appointment.doctorId == doctorId && appointment.patientId == userId
                                    && appointment.startTime == args.appointment.startTime && appointment.endTime == args.appointment.endTime ) {
                                    // Found the matching appointment
                                    appointmentSnapshot.ref.child("feedbackGiven").setValue(true)
                                    break // Exit loop after updating the first matching record
                                }
                            }

                            findNavController().popBackStack()
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle errors
                        }
                    })

                } else {

                }
            }


        //set feedback given == true

    }

    fun getFeedback() {
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