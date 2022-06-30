package com.shashank.wosafe.auth

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.compose.ui.unit.TextUnit
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.shashank.wosafe.R
import com.shashank.wosafe.fragmentExtenions.acceleration
import com.shashank.wosafe.fragmentExtenions.currentAcceleration
import com.shashank.wosafe.fragmentExtenions.lastAcceleration
import com.shashank.wosafe.fragmentExtenions.shakeDetect
import kotlinx.android.synthetic.main.fragment_otp_screen.*
import kotlinx.android.synthetic.main.fragment_register_screen.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.util.*
import java.util.concurrent.TimeUnit

const val REQUEST_CODE = 0;

class RegisterScreenFragment :Fragment(R.layout.fragment_register_screen)  {
     var mAuth = FirebaseAuth.getInstance()
    lateinit var storedVerificationId:String
    lateinit var mobileNumber: String
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks



    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)







        emergencyBtn.setOnClickListener {

         val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
             .requestIdToken(getString(R.string.weblient))
             .requestEmail()
             .requestProfile()
             .build()

           val signInClient =GoogleSignIn.getClient(requireActivity(),options)
            signInClient.signInIntent.also {
                startActivityForResult(it, REQUEST_CODE)
            }




        }


        registerBtn.setOnClickListener {
            login()

        }


        callbacks =object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(activity, "${p0.localizedMessage.toString()}", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(verificationId, token)
                storedVerificationId = verificationId
                val bundle =bundleOf("verificationCode" to storedVerificationId)
                findNavController().navigate(R.id.action_registerScreenFragment_to_otpScreenFragment,bundle)




            }

        }




    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode== REQUEST_CODE){
            val account =GoogleSignIn.getSignedInAccountFromIntent(data).result
            account?.let {
                googleAuthForFirebase(it)
            }
        }
    }

    private fun googleAuthForFirebase(it: GoogleSignInAccount) {

        val credential= GoogleAuthProvider.getCredential(it.idToken,null)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                mAuth.signInWithCredential(credential)


            }catch (err:Exception){
                Toast.makeText(requireActivity(), "${err.localizedMessage.toString()}", Toast.LENGTH_SHORT).show()
            }
        }

    }


    override fun onResume() {

        super.onResume()
    }

    override fun onPause() {

        super.onPause()
    }


    fun login() {

        // get the phone number from edit text and append the country cde with it
        mobileNumber = phoneEdt.text.toString()
        if (mobileNumber.isNotEmpty()){

            mobileNumber = "+91$mobileNumber"
            sendVerificationCode(mobileNumber)
        }else{
            Toast.makeText(requireActivity(),"Enter mobile number", Toast.LENGTH_SHORT).show()
        }
    }

     fun sendVerificationCode(mobileNumber: String) {

         val options = PhoneAuthOptions.newBuilder(mAuth)
             .setPhoneNumber(mobileNumber)       // Phone number to verify
             .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
             .setActivity(requireActivity())                 // Activity (for callback binding)
             .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
             .build()
         PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun onStart() {
        super.onStart()

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





}