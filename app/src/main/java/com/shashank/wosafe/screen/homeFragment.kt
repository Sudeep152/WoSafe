package com.shashank.wosafe.screen


import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.shashank.wosafe.R
import com.shashank.wosafe.fragmentExtenions.shakeDetect
import java.util.*


class homeFragment : Fragment() {

    lateinit var lastLocation:Location
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->


        val sydney = LatLng(-34.0, 151.0)

        try {

            val success = googleMap.setMapStyle(
                context?.let {
                    MapStyleOptions.loadRawResourceStyle(
                        it, R.raw.style_json)
                })
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }



        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(requireActivity())






        googleMap.uiSettings.isZoomControlsEnabled=true



        if (ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return@OnMapReadyCallback
        }
        googleMap.isMyLocationEnabled=true
        fusedLocationProviderClient.lastLocation.addOnCompleteListener(requireActivity()) { location->
            if(location !=null){
                lastLocation= location.result
                val currentLatLng= LatLng(lastLocation.latitude,lastLocation.longitude)
               googleMap.addMarker(MarkerOptions().position(currentLatLng)
                    .title("Iam here").icon(
                        getBitmapDescriptorFromVector(requireContext(), R.drawable.ic_woman_marker)))

                val geocoder: Geocoder
                val addresses: List<Address>
                geocoder = Geocoder(requireActivity(), Locale.getDefault())

                addresses = geocoder.getFromLocation(lastLocation.latitude,
                    lastLocation.longitude,
                    1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


                val address: String = addresses[0].getAddressLine(0)

                Toast.makeText(requireActivity(), "${address}", Toast.LENGTH_SHORT).show()
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,12f))
                googleMap.maxZoomLevel
            }
        }



    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)





        if ( shakeDetect(requireActivity())){

            Toast.makeText(requireActivity(), "hii", Toast.LENGTH_SHORT).show()
        }



        val transitions = mutableListOf<ActivityTransition>()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()



        val request = ActivityTransitionRequest(transitions)






    }




    fun getBitmapDescriptorFromVector(context: Context, @DrawableRes vectorDrawableResourceId: Int): BitmapDescriptor? {

        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        val bitmap = Bitmap.createBitmap(vectorDrawable!!.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}