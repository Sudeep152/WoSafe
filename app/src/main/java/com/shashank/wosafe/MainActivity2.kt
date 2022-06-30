package com.shashank.wosafe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
         supportActionBar?.hide()
        bottomNavigationView.apply {
            background=null
            menu.getItem(2).isEnabled=false}

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        bottomNavigationView.setupWithNavController(navController)

        fabNewPost.setOnClickListener {

            zoom(fabNewPost)
        }


        }
    fun zoom(view: View?) {

        val animation1: Animation = AnimationUtils.loadAnimation(this,
            R.anim.zoom_animation)
        view?.startAnimation(animation1)
    }
    }

