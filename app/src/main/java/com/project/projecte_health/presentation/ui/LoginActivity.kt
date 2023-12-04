package com.project.projecte_health.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.values
import com.project.projecte_health.base.BaseActivity
import com.project.projecte_health.data.local.PrefsManager
import com.project.projecte_health.databinding.ActivityLoginBinding
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

                            lifecycleScope.launch {
                                prefsManager.saveUserId(userId)
                            }

                            val intent = Intent(
                                this@LoginActivity,
                                DashboardActivity::class.java
                            )
                            startActivity(intent)
                            finish()
                            userReference.addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        // Access user details
                                        val name =
                                            snapshot.child("name").getValue(String::class.java)
                                        if (name != null) {
                                            // Now 'name' contains the user's name
                                            val intent = Intent(
                                                this@LoginActivity,
                                                DashboardActivity::class.java
                                            )
                                            intent.putExtra("name", name)
                                            startActivity(intent)
                                            finish()
                                            Log.d("SignInActivity", "User's name: $name")
                                        } else {
                                            Log.d("SignInActivity", "Name not found in database")
                                        }

                                        // Continue with your logic or UI updates
                                    } else {
                                        // User data does not exist
                                        Log.d("SignInActivity", "User data not found in database")
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Handle database error
                                    Log.e("SignInActivity", "Database error: ${error.message}")
                                }
                            })


//                        Log.d("SignInActivity", "signInWithEmail:success")
//                        val user = auth.currentUser
//
//                        // Retrieve additional details from Firebase Realtime Database
//                        user?.let {
//                            val userId = it.uid
//                            val userReference = database.reference.child("users").child(userId)
//
//                            userReference.addListenerForSingleValueEvent(object :
//                                ValueEventListener {
//                                override fun onDataChange(snapshot: DataSnapshot) {
//
//                                    val name = snapshot.child("name").value.toString()
//
//                                    Log.d("SignInActivity", "Name from database: $name")
//                                    lifecycleScope.launch {
//                                        prefsManager.saveUserId(userId)
//                                    }
//
//                                    // Access user details
//                                    val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
//                                    intent.putExtra("name", name)
//                                    startActivity(intent)
//                                    finish()
//                                }
//
//                                override fun onCancelled(error: DatabaseError) {
//                                    // Handle database error
//                                }
//                            })
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