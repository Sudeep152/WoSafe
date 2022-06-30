package com.shashank.wosafe.auth


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.shashank.wosafe.R
import kotlinx.android.synthetic.main.fragment_otp_screen.*


class OtpScreenFragment :Fragment(R.layout.fragment_otp_screen){

    lateinit var mAuth:FirebaseAuth
    lateinit var code:String

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth= FirebaseAuth.getInstance()


        code =arguments?.getString("verificationCode").toString()


        continueBtn.setOnClickListener {

            val otp = otp_view.otp.toString()

            if (otp.isEmpty()){
                Toast.makeText(requireActivity(), "please enter opt", Toast.LENGTH_SHORT).show()
            }else{
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    code, otp)
                signInWithPhoneAuthCredential(credential)
                Toast.makeText(requireActivity(), "${arguments?.getString("verificationCode")}", Toast.LENGTH_SHORT).show()
            }
//            if (otp_view.otp.toString()==null){
//                Toast.makeText(requireActivity(), "please enter opt", Toast.LENGTH_SHORT).show()
//            }else{
//
//            }

        }
    }




    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener { task->
            if (task.isSuccessful) {
                findNavController().navigate(R.id.action_otpScreenFragment_to_moreAboutYouFragment)


            } else {
                // Sign in failed, display a message and update the UI
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    // The verification code entered was invalid
                    Toast.makeText(requireActivity(),"Invalid OTP", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }
}

