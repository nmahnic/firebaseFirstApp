package com.nicomahnic.firebase.fireTest.fragments.mainActivity


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.nicomahnic.firebase.fireTest.R
import com.nicomahnic.firebase.fireTest.activities.HomeActivity
import com.nicomahnic.firebase.fireTest.databinding.FragmentLoginScreenBinding
import com.nicomahnic.firebase.fireTest.utils.ProviderType
import java.lang.Exception

class LoginScreen : Fragment(R.layout.fragment_login_screen) {

    private lateinit var b: FragmentLoginScreenBinding
    private lateinit var edtPasswd: String
    private lateinit var edtEmail: String

    companion object{
        const val GOOGLE_SIGN_IN = 100
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b = FragmentLoginScreenBinding.bind(view)

        (activity as AppCompatActivity).supportActionBar?.show()

        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        val bundle = Bundle()
        bundle.putString("message","Integracion de Firebase completa")
        analytics.logEvent("NM", bundle)

    }


    override fun onStart() {
        super.onStart()

        b.progressBar.visibility = View.GONE
        b.authLayout.visibility = View.VISIBLE

        session()
        setup()
    }

    private fun session(){
        val prefs = requireContext().getSharedPreferences(
            getString(R.string.pref_file),
            Context.MODE_PRIVATE
        )

        val email = prefs.getString("email",null)
        val provider = prefs.getString("provider",null)
        if(email != null && provider != null){
            b.authLayout.visibility = View.INVISIBLE
            b.progressBar.visibility = View.VISIBLE
            showHome(email, ProviderType.valueOf(provider))
        }
    }

    private fun setup(){
        (activity as AppCompatActivity).supportActionBar?.title = "Autenticación"

        b.btnSign.setOnClickListener {
            if(b.edtEmail.text.isNotEmpty() && b.edtPasswd.text.isNotEmpty()){
                edtEmail = b.edtEmail.text.toString()
                edtPasswd = b.edtPasswd.text.toString()
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    edtEmail,
                    edtPasswd
                ).addOnCompleteListener {
                    if(it.isSuccessful)
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    else
                        it.exception?.message?.let { message -> showAlert(message) }
                }
            }else{
                showAlert("Email or Password is empty")
            }
        }

        b.btnLogin.setOnClickListener {
            if(b.edtEmail.text.isNotEmpty() && b.edtPasswd.text.isNotEmpty()){
                edtEmail = b.edtEmail.text.toString()
                edtPasswd = b.edtPasswd.text.toString()
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    edtEmail,
                    edtPasswd
                ).addOnCompleteListener {
                    if(it.isSuccessful)
                        showHome(it.result?.user?.email ?: "",ProviderType.BASIC)
                    else
                        it.exception?.message?.let { message -> showAlert(message) }
                }
            }else{
                showAlert("Email or Password is empty")
            }
        }

        b.btnGoogleSign.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(requireContext(),googleConf)
            googleClient.signOut() //si no se pone esto se necesita AuthUI para el logout
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                if(account != null){
                    val credential = GoogleAuthProvider.getCredential(account.idToken,null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if(it.isSuccessful){
                                showHome(account.email ?: "",ProviderType.GOOGLE)
                            }else{
                                it.exception?.message?.let { message -> showAlert(message) }
                            }
                        }
                }
            }catch (e: ApiException){
                Log.e("NM",e.toString())
                showAlert(e.toString())
            }

        }
    }

    private fun showAlert(exceptionMessage: String){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        builder.setMessage(exceptionMessage)
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType){
        val sendIntent = Intent(context, HomeActivity::class.java).apply {
            putExtra("email",email)
            putExtra("provider",provider.name)
        }
        startActivity(sendIntent)
//        requireActivity().finish()
    }
}