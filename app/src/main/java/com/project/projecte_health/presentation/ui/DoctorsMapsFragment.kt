package com.project.projecte_health.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.project.projecte_health.R
import com.project.projecte_health.databinding.FragmentDoctorsMapsBinding

class DoctorsMapsFragment : Fragment() {

    lateinit var binding : FragmentDoctorsMapsBinding
    private val args : DoctorsMapsFragmentArgs by navArgs()

    private val callback = OnMapReadyCallback { googleMap ->

        val sydney = LatLng(args.docInfo.latLng.latitude, args.docInfo.latLng.longitude)
        googleMap.addMarker(MarkerOptions().position(sydney).title(args.docInfo.name))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15.0f))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDoctorsMapsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)


        binding.heading.headingTitle.text = "Find Your Doctor"

        binding.heading.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}