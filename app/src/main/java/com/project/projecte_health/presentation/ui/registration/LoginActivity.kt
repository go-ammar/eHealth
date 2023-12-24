package com.project.projecte_health.presentation.ui.registration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.project.projecte_health.base.BaseActivity
import com.project.projecte_health.databinding.ActivityLoginBinding
import com.project.projecte_health.presentation.ui.doctors.DoctorsDashboardActivity
import com.project.projecte_health.presentation.ui.patients.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionViews()
    }

    private fun actionViews() {


        binding.loginBtn.setOnClickListener {

            auth.signInWithEmailAndPassword(
                binding.usernameEt.text.toString(),
                binding.passwordEt.text.toString()
            )
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Login success

                        val user = auth.currentUser
                        user?.let {
                            val userId = it.uid
                            val userReference = database.reference.child("users").child(userId)
                            Log.d("SignInActivity", "before User's name: ")

//                            val userData = HashMap<String, Any>()
//                            userData["FCMToken"] = token
//
//                            userReference.updateChildren(userData)

                            lifecycleScope.launch {
                                prefsManager.saveUserId(userId)
                            }
                            FirebaseMessaging.getInstance().subscribeToTopic(userId)

                            userReference.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // Access user details
                                        val name =
                                            dataSnapshot.child("name").getValue(String::class.java)

                                        lifecycleScope.launch {
                                            prefsManager.saveName(name.toString())
                                        }

                                        val userType =
                                            dataSnapshot.child("userType")
                                                .getValue(String::class.java)
                                        if (name != null) {
                                            // Now 'name' contains the user's name
                                            if (userType == "Doctor") {
                                                val intent = Intent(
                                                    this@LoginActivity,
                                                    DoctorsDashboardActivity::class.java
                                                )
                                                startActivity(intent)
                                                finish()
                                            } else {
                                                val intent = Intent(
                                                    this@LoginActivity,
                                                    DashboardActivity::class.java
                                                )
                                                startActivity(intent)
                                                finish()
                                            }

                                            Log.d("SignInActivity", "User's name: $name")
                                        } else {
                                            Log.d("SignInActivity", "Name not found in database")
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }


                            })
                        }
                        // You can navigate to another activity or perform other actions here
                    } else {
                        // Login failed
                        Log.w("SignInActivity", "signInWithEmail:failure", task.exception)
                        // Handle errors, display a message, etc.
                    }
                }

        }


    }


}