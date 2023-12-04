package com.project.projecte_health.base

import android.content.Context
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.project.projecte_health.data.local.PrefsManager
import javax.inject.Inject

abstract class BaseFragment : Fragment() {

    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase

//    @Inject
//    lateinit var prefsManager: PrefsManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance()

    }

}