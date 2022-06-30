package com.shashank.wosafe.auth


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.opengl.Visibility
import android.os.Bundle
import android.provider.Settings
import android.telephony.emergency.EmergencyNumber
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.unit.TextUnit
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shashank.wosafe.MainActivity2
import com.shashank.wosafe.R
import com.shashank.wosafe.model.Women
import kotlinx.android.synthetic.main.fragment_aboutu.*
import kotlinx.android.synthetic.main.fragment_otp_screen.*
import java.util.*


class moreAboutYouFragment :Fragment(R.layout.fragment_aboutu) {

   var mAuth= FirebaseAuth.getInstance()
    lateinit var database :FirebaseFirestore
    private lateinit var currentLocation:Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var name :String
    private lateinit var email:String
    private lateinit var addressData: String
    private lateinit var emergencyNumberOne:String
    private lateinit var emergencyNumberTwo:String
    private lateinit var womenMobile :String


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Toast.makeText(requireActivity(), "${mAuth.currentUser?.phoneNumber.toString()}", Toast.LENGTH_SHORT).show()
        val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){granted->
            if (granted){
                finishBtn.setOnClickListener {

                             upload_data()

                }
            }else{
                showSettingsAlert()
                Toast.makeText(requireActivity(), "Permission Denied", Toast.LENGTH_SHORT).show()

            }



        }


        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(requireActivity())




       database = FirebaseFirestore.getInstance()


        getCurrentAddress.setOnClickListener {



            if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){

                Toast.makeText(requireContext(), "Permission Done", Toast.LENGTH_SHORT).show()
                fusedLocationProviderClient.lastLocation.addOnCompleteListener {

                    if(it !=null){
                        currentLocation=it.result
                        val geocoder: Geocoder
                        val addresses: List<Address>
                        geocoder = Geocoder(requireActivity(), Locale.getDefault())
                        val  locationRequest = LocationRequest()
                        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                        addresses = geocoder.getFromLocation(currentLocation.latitude,
                            currentLocation.longitude,
                            1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


                        val address: String = addresses[0].getAddressLine(0)

                        getCurrentAddress.setText("$address")
                        addressData=address


                    }
                }
                name = nameEdt.text.toString()
                email= emailEdt.text.toString()
                emergencyNumberOne= emergencyOne.text.toString()
                emergencyNumberTwo= emergencyTwo.text.toString()
                womenMobile= mAuth.currentUser?.phoneNumber.toString()

                requestPermission.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)

            }else{
                requestPermission.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }

    }
    private fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(requireActivity())
        alertDialog.setTitle("SETTINGS")
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?")
        alertDialog.setPositiveButton("Settings") { _, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            this.startActivity(intent)
        }
        alertDialog.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        alertDialog.show()
    }

    fun upload_data(){

        if(TextUtils.isEmpty(name)|| TextUtils.isEmpty(email) ||TextUtils.isEmpty(emergencyNumberOne) || TextUtils.isEmpty(addressData)||TextUtils.isEmpty(emergencyNumberTwo)){
            Toast.makeText(requireActivity(), "please enter all fields", Toast.LENGTH_SHORT).show()
        }else{
            val data = database.collection("Woman").document(womenMobile)
            val women = Women(name, email,addressData,womenMobile,emergencyNumberOne,emergencyNumberTwo)
            data.set(women).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(requireActivity(), "Welcome superwoman $name", Toast.LENGTH_SHORT).show()
                    Intent(requireContext(), MainActivity2::class.java).also {
                        startActivity(it)
                        requireActivity().finish()
                    }
                }else{
                    Toast.makeText(requireActivity(), task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                }

            }

        }


    }



}