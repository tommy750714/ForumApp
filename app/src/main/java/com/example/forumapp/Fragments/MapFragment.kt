package com.example.forumapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.forumapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    companion object {
        lateinit var mapFragment : SupportMapFragment
        val TAG: String = MapFragment::class.java.simpleName
        fun newInstance() = MapFragment ()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var rootView = inflater.inflate(R.layout.fragment_map, container, false)

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return rootView
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap!!

        // Add a marker in Sydney and move the camera
        val petHospital = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(petHospital).title("Pet Hospital"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(petHospital))
    }


}