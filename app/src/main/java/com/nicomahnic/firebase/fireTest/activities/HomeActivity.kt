package com.nicomahnic.firebase.fireTest.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nicomahnic.firebase.fireTest.R

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    object User{
        var email = ""
        var provider = ""
    }
}