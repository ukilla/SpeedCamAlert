package com.example.speedcamalert.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.speedcamalert.R
import com.example.speedcamalert.databinding.FragmentLoginBinding
import com.example.speedcamalert.viewmodels.LoggedUserViewModel
import java.security.MessageDigest


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var databaseUser: DatabaseReference?=null
    private val loggedUserViewModel: LoggedUserViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()


        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = requireActivity().getSharedPreferences("SpeedCamAlert", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            Navigation.findNavController(binding.root).navigate(R.id.action_loginFragment_to_homeFragment)
        }
        binding.registerNow.setOnClickListener{
            Navigation.findNavController(binding.root).navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.btnLogin.setOnClickListener{
            Logovanje();
        }

    }
    private fun saveLoginState(username: String, password: String) {
        val sharedPreferences = requireContext().getSharedPreferences("SpeedCamAlert", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        sharedPreferences.edit().putString("username",username ).apply()
        sharedPreferences.edit().putString("password",password ).apply()
    }
    private fun Logovanje() {
        val editUsername = requireView().findViewById<EditText>(R.id.loginUsername)
        val editSifra = requireView().findViewById<EditText>(R.id.loginPassword)
        val username = editUsername.text.toString()
        val sifra = hashPassword(editSifra.text.toString())

        if (username.isNotEmpty() && sifra.isNotEmpty()) {
            val databaseUser = FirebaseDatabase.getInstance("https://speedcamalert-3461b-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")
            databaseUser.child(username).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val dataSnapshot = task.result
                    if (dataSnapshot.exists()) {
                        val dataSnapshot = task.result
                        val username: String = dataSnapshot.child("username").getValue(String::class.java)!!
                        val sifra2: String = dataSnapshot.child("password").getValue(String::class.java)!!
                        if(sifra2==sifra)
                        {
                            saveLoginState(username,sifra)
                            //loggedUserViewModel.login(username)
                            Navigation.findNavController(binding.root).navigate(R.id.action_loginFragment_to_homeFragment)
                        }else{
                            Toast.makeText(this.activity,"Pogresna lozinka", Toast.LENGTH_SHORT).show()
                        }
                    } else {

                        Toast.makeText(this.activity,"Ne postoji nalog sa zadataim korisnickim imenom",Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle task exception or error
                    val exception = task.exception
                    // Log or display the error message
                    exception?.message?.let { errorMessage ->
                        Log.e("Firebase", errorMessage)
                    }
                }
            }




        }else {
            val activityObj: Activity? = this.activity
            Toast.makeText(activityObj, "Unesite sve podatke", Toast.LENGTH_LONG).show()
        }
    }
    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hashedBytes = md.digest(password.toByteArray(Charsets.UTF_8))
        return bytesToHex(hashedBytes)
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexArray = "0123456789ABCDEF".toCharArray()
        val hexChars = CharArray(bytes.size * 2)
        for (i in bytes.indices) {
            val v = bytes[i].toInt() and 0xFF
            hexChars[i * 2] = hexArray[v.ushr(4)]
            hexChars[i * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }
}