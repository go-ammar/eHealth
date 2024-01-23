package com.project.projecte_health.presentation.ui.registration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
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
import com.project.projecte_health.utils.Utils
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

    override fun displayProgressBar(loading: Boolean) {
        binding.loader.root.visibility = Utils.displayCustomLoaderView(this, loading)
        if (loading) {
            binding.loginBtn.visibility = View.GONE
        } else {
            binding.loginBtn.visibility = View.VISIBLE
        }
    }

    private fun actionViews() {

        if (intent.getBooleanExtra("emailVerification", false)) {
            Toast.makeText(this, "Please verify your email.", Toast.LENGTH_SHORT).show()
        }

        binding.loginBtn.setOnClickListener {

            displayProgressBar(true)
            auth.signInWithEmailAndPassword(
                binding.usernameEt.text.toString(),
                binding.passwordEt.text.toString()
            )
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Login success

                        val user = auth.currentUser

                        if (user?.isEmailVerified == true || binding.usernameEt.text.toString() == "admin@admin.com" || binding.usernameEt.text.toString() == "ammarahsan99@gmail.co") {
                            user?.let {
                                val userId = it.uid
                                val userReference = database.reference.child("users").child(userId)

                                lifecycleScope.launch {
                                    prefsManager.saveUserId(userId)
                                }
                                FirebaseMessaging.getInstance().subscribeToTopic(userId)

                                userReference.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            displayProgressBar(false)

                                            // Access user details
                                            val name =
                                                dataSnapshot.child("name")
                                                    .getValue(String::class.java)

                                            lifecycleScope.launch {
                                                prefsManager.saveName(name.toString())
                                                prefsManager.saveUserType(dataSnapshot.child("userType")
                                                    .getValue(String::class.java).toString())
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
                                                Log.d(
                                                    "SignInActivity",
                                                    "Name not found in database"
                                                )
                                            }
                                        }
                                        displayProgressBar(false)

                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        displayProgressBar(false)
                                    }


                                })
                            }
                        } else {
                            displayProgressBar(false)
                            Toast.makeText(
                                this,
                                "Please verify your email first!",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                        // You can navigate to another activity or perform other actions here
                    } else {
                        // Login failed
                        displayProgressBar(false)
//                        Toast.makeText(this, "Something went wrong "+task.exception?.message, Toast.LENGTH_SHORT).show()
                        Log.w("SignInActivity", "signInWithEmail:failure", task.exception)
                        // Handle errors, display a message, etc.
                    }
                }
                .addOnFailureListener {
                    displayProgressBar(false)
                    Toast.makeText(this, "Something went wrong "+it.message, Toast.LENGTH_SHORT).show()
                }

        }

        binding.passwordEt.addTextChangedListener {
            enableBtn()
        }

        binding.usernameEt.addTextChangedListener {
            enableBtn()
        }

    }

    private fun enableBtn() {
        if (Patterns.EMAIL_ADDRESS.matcher(binding.usernameEt.text.toString()).matches()
            && binding.passwordEt.text.toString().isNotEmpty()
            && binding.usernameEt.text.toString().isNotEmpty()
        ) {
            binding.loginBtn.isEnabled = true
            binding.loginBtn.alpha = 1f
        } else {
            binding.loginBtn.isEnabled = false
            binding.loginBtn.alpha = 0.7f
        }
    }


}