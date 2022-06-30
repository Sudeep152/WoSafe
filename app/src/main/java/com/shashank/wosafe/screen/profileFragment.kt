package com.shashank.wosafe.screen

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.shashank.wosafe.R
import com.shashank.wosafe.model.Women
import kotlinx.android.synthetic.main.fragment_profie.*


class profileFragment:Fragment(R.layout.fragment_profie) {

    private lateinit var mAuth :FirebaseAuth
    lateinit var database : FirebaseFirestore
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth= FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()



        var dof = database.collection("Woman").document(mAuth.currentUser?.phoneNumber.toString())

          dof.get().addOnSuccessListener {

                val user = it.toObject<Women>()
              womenNameEdt.setText(user?.name)

            }




        signOutBtn.setOnClickListener {
            mAuth.signOut()
            Toast.makeText(activity,"Bye bye super women",Toast.LENGTH_SHORT).show()
        }
    }
}

