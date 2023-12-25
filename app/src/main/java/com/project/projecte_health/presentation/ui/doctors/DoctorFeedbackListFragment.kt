package com.project.projecte_health.presentation.ui.doctors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.project.projecte_health.R
import com.project.projecte_health.base.BaseFragment
import com.project.projecte_health.data.local.bookings.Feedback
import com.project.projecte_health.databinding.FragmentFeedbackListBinding
import com.project.projecte_health.presentation.ui.adapters.FeedbackListAdapter
import com.project.projecte_health.presentation.ui.patients.AppointmentsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DoctorFeedbackListFragment : BaseFragment() {


    private lateinit var binding: FragmentFeedbackListBinding
    private lateinit var adapter: FeedbackListAdapter
    private var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFeedbackListBinding.inflate(layoutInflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            userId = (activity as DoctorFeedbackActivity).prefsManager.getUserId().toString()

            getFeedbackForDoctor(userId) {

                adapter = FeedbackListAdapter()
                adapter.submitList(it)
                binding.feedbackRv.adapter = adapter

            }

        }

    }

    fun getFeedbackForDoctor(doctorId: String, callback: (List<Feedback>) -> Unit) {
        val feedbackRef = database.getReference("feedback").child(doctorId)

        feedbackRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val feedbackList = mutableListOf<Feedback>()

                for (feedbackSnapshot in snapshot.children) {
                    val rating = feedbackSnapshot.child("rating").getValue(Int::class.java)
                    val feedbackText =
                        feedbackSnapshot.child("feedback").getValue(String::class.java)
                    val patientId = feedbackSnapshot.child("patientId").getValue(String::class.java)

                    if (rating != null && feedbackText != null && patientId != null) {
                        val feedback = Feedback(rating, feedbackText, patientId)
                        feedbackList.add(feedback)
                    }
                }

                // Invoke the callback with the list of feedback entries
                callback(feedbackList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                println("Error getting feedback: $error")
                // Invoke the callback with an empty list or handle accordingly
                callback(emptyList())
            }
        })
    }


}