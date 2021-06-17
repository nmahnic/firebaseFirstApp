package com.nicomahnic.firebase.fireTest.fragments.homeActivity

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.nicomahnic.firebase.fireTest.R
import com.nicomahnic.firebase.fireTest.activities.HomeActivity.User
import com.nicomahnic.firebase.fireTest.databinding.FragmentHomeScreenBinding


class HomeScreen : Fragment(R.layout.fragment_home_screen) {

    private lateinit var b: FragmentHomeScreenBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b = FragmentHomeScreenBinding.bind(view)

        User.email = activity?.intent?.getStringExtra("email").toString()
        User.provider = activity?.intent?.getStringExtra("provider").toString()

        setup(User.email, User.provider)

        //Guardado de datos
        val prefs = requireContext().getSharedPreferences(
            getString(R.string.pref_file),
            Context.MODE_PRIVATE
        ).edit()
        prefs.putString("email", User.email)
        prefs.putString("provider", User.provider)
        prefs.apply()
    }

    private fun setup(email: String, provider: String) {
        (activity as AppCompatActivity).supportActionBar?.title = "Inicio"

        b.tvEmail.text = email
        b.tvProvider.text = provider

        b.btnLogout.setOnClickListener {

            val prefs = requireContext().getSharedPreferences(
                getString(R.string.pref_file),
                Context.MODE_PRIVATE
            ).edit()
            prefs.clear()
            prefs.apply()


//            AuthUI.getInstance().signOut(requireContext())
            FirebaseAuth.getInstance().signOut()
            activity?.onBackPressed()
        }
    }
}