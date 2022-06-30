package com.shashank.wosafe.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shashank.wosafe.MainActivity2
import com.shashank.wosafe.R
import kotlinx.android.synthetic.main.fragment_otp_screen.*
import kotlinx.android.synthetic.main.fragment_splash_screen.*


@SuppressLint("CustomSplashScreen")
class SplashScreenFragment : Fragment(R.layout.fragment_splash_screen){
   var mAuth = FirebaseAuth.getInstance()
    lateinit var database : FirebaseFirestore
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseFirestore.getInstance()

        zoom(ic_logo)
    }

    override fun onStart() {
        super.onStart()

//val doc = mAuth.currentUser?.phoneNumber?.let { database.collection("Women").document(it).get() }
        if (mAuth.currentUser?.email !=null){

            Intent(requireContext(), MainActivity2::class.java).also {
                startActivity(it)
                requireActivity().finish()
            }
        }
        else{

            val handler = Handler()
            handler.postDelayed({

                findNavController().navigate(R.id.action_splashScreenFragment_to_registerScreenFragment)
            },1000)


        }










        val lm = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
            continueBtn.setOnClickListener {
                findNavController().navigate(R.id.action_otpScreenFragment_to_moreAboutYouFragment)
            }

        } catch (ex: Exception) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder(context)
                .setMessage("Please enable ")
                .setPositiveButton("Click me",
                    DialogInterface.OnClickListener { paramDialogInterface, paramInt ->
                        requireContext().startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    })
                .setNegativeButton("Cancel", null)
                .show()
        }

    }


    fun zoom(view: View?) {

        val animation1: Animation = AnimationUtils.loadAnimation(this.context,
            R.anim.zoom_animation)
        view?.startAnimation(animation1)
    }
}


