package com.project.projecte_health.presentation.ui.doctors

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.project.projecte_health.base.BaseFragment
import com.project.projecte_health.data.local.users.ProfileModel
import com.project.projecte_health.databinding.FragmentDoctorProfileBinding
import com.project.projecte_health.utils.Utils.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.util.ArrayList


@AndroidEntryPoint
class DoctorProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentDoctorProfileBinding
    lateinit var userId: String
    private var daysList: MutableList<String> = ArrayList()

    init {
        daysList.add("Monday")
        daysList.add("Tuesday")
        daysList.add("Wednesday")
        daysList.add("Thursday")
        daysList.add("Friday")
        daysList.add("Saturday")
        daysList.add("Sunday")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentDoctorProfileBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment

        lifecycleScope.launch {
            userId = (activity as DocAccountActivity).prefsManager.getUserId().toString()
            val userRef = database.reference.child("users/$userId")

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Access the value from the DataSnapshot
                        val profileData = snapshot.getValue(ProfileModel::class.java)
                        if (profileData != null) {

                            binding.tvProfileName.text = profileData.name

                            requireActivity().runOnUiThread {
                                // Your UI update code here
                                if (profileData.imageUrl?.isNotEmpty() == true) {
                                    Glide.with(requireContext())
                                        .load(profileData.imageUrl)
                                        .into(binding.ivProfile)
                                }

                                val daysList: List<String>? = profileData.availability?.days// Your ArrayList of days

                                val daysString = daysList?.joinToString(", ") // Join days with commas

                                binding.daysTv.text = "Days: $daysString"

                                binding.startTimeTv.text =
                                    "Start time: " + profileData.availability?.startTime
                                binding.endTimeTv.text =
                                    "Start time: " + profileData.availability?.endTime
                                binding.descriptionDetails.text = profileData.bio

                            }


                            binding.editBtn.setOnClickListener {
                                val action =
                                    DoctorProfileFragmentDirections.actionDoctorProfileFragmentToDoctorEditProfileFragment(profileData)
                                findNavController().safeNavigate(action)
                            }
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
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnEdit.setOnClickListener {
            checkPermission()
        }

    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT > 32) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_VIDEO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestStoragePermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                imagePickerIntent()
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                imagePickerIntent()
            }
        }
    }


    private fun imagePickerIntent() {
        pickImageLauncher.launch(
            Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
        )
    }

    private val requestStoragePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                imagePickerIntent()
            } else {
                checkPermission()
            }
        }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Image is successfully picked
            val selectedImageUri: Uri? = result.data?.data

            // Display the selected image in an ImageView
//            binding.ivProfile.setImageURI(selectedImageUri)

            requireActivity().runOnUiThread {
                // Your UI update code here
                Glide.with(requireContext())
                    .load(selectedImageUri)
                    .into(binding.ivProfile)

            }

            // Get a reference to the Firebase Storage instance
            val storage = FirebaseStorage.getInstance()

            // Create a reference to the storage location where you want to store the image (e.g., "images" folder)
            val storageRef: StorageReference = storage.reference.child("images")

            // Create a reference to the specific image file (e.g., "image.jpg")
            val imageRef: StorageReference = storageRef.child("$userId.jpg")

            // Get the image file as a byte array
            val imageByteArray: ByteArray? =
                selectedImageUri?.let { uriToByteArray(requireContext(), it) }

            if (imageByteArray != null) {
                imageRef.putBytes(imageByteArray)
                    .addOnSuccessListener {
                        // Image upload successful
                        // You can retrieve the download URL or perform other actions
                        imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                            val imageUrl = downloadUri.toString()
                            saveImageToFirebase(imageUrl)
                        }
                    }
                    .addOnFailureListener { exception ->
                        println("Image upload failed: $exception")
                    }
            }
        }
    }

    private fun saveImageToFirebase(imageUrl: String) {
        Log.d("TAG", ":ImageUrl " + imageUrl)

        val userData = HashMap<String, Any>()
        userData["imageUrl"] = imageUrl
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        try {
            database.reference.child("users").child(userId).updateChildren(userData)
                .addOnSuccessListener {
                    // Update successful
                    Log.d("TAG", "Image URL updated successfully")
                }
                .addOnFailureListener { error ->
                    // Handle the failure
                    Log.e("TAG", "Error updating image URL: $error")
                }
        } catch (e: Exception) {
            Log.e("TAG", "saveImageToFirebase: ")
        }
    }


    fun uriToByteArray(context: Context, uri: Uri): ByteArray? {
        var inputStream: InputStream? = null
        var byteArray: ByteArray? = null
        try {
            // Open an input stream from the Uri using ContentResolver
            val contentResolver: ContentResolver = context.contentResolver
            inputStream = contentResolver.openInputStream(uri)

            // Read the data into a byte array
            if (inputStream != null) {
                byteArray = readBytes(inputStream)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return byteArray
    }

    @Throws(IOException::class)
    private fun readBytes(inputStream: InputStream): ByteArray {
        // Use a ByteArrayOutputStream to read data into a byte array
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len: Int
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }

}