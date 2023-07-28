package com.example.speedcamalert.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.example.speedcamalert.R
import com.example.speedcamalert.classes.User
import com.example.speedcamalert.databinding.FragmentRegisterBinding
import java.io.FileNotFoundException
import java.io.InputStream
import java.security.MessageDigest
import kotlin.random.Random


class RegisterFragment : Fragment() {

    private val random = Random(System.currentTimeMillis())
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null
    private var databaseUser:DatabaseReference?=null
    private var downloadUrl:String=""
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Registrovanje: 0 %")
        progressDialog.setCancelable(false)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.progress = 0
        progressDialog.max = 100
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnAddImage.setOnClickListener{
            otvoriGaleriju()
        }
        binding.buttonRegister.setOnClickListener{
            register() }
        binding.btnRedirectLogin.setOnClickListener{
            Navigation.findNavController(binding.root).navigate(R.id.action_registerFragment_to_loginFragment)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data!!
            try
            {
                val imageStream: InputStream? = requireActivity().contentResolver.openInputStream(selectedImageUri!!)
                val selectedImageBitmap = BitmapFactory.decodeStream(imageStream)
                binding.imageView.setImageBitmap(selectedImageBitmap)

            }
            catch(e: FileNotFoundException)
            {
                e.printStackTrace();
            }
        }
    }
    private fun register() {
        val editIme = requireView().findViewById<EditText>(R.id.editTextIme)
        val editPrezime = requireView().findViewById<EditText>(R.id.editTextPrezime)
        val editUsername = requireView().findViewById<EditText>(R.id.editTextKorisnickoIme)
        val editPassword = requireView().findViewById<EditText>(R.id.editTextSifra)
        val editPassword2 = requireView().findViewById<EditText>(R.id.editTextSifra2)
        val editBroj = requireView().findViewById<EditText>(R.id.editTextPhoneNumber)


        val firstName = editIme.text.toString()
        val lastName = editPrezime.text.toString()
        val username = editUsername.text.toString()
        val password = hashPassword(editPassword.text.toString())
        val password2 = hashPassword(editPassword2.text.toString())
        val phoneNumber = editBroj.text.toString()

        if (firstName != "" && lastName != "" && username != "" && password != "" && phoneNumber != "" && password2!="") {
            databaseUser =
                FirebaseDatabase.getInstance("https://speedcamalert-3461b-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("Users")

            val storageRef= FirebaseStorage.getInstance().getReference();
            val stringBuilder = StringBuilder()
            for (i in 1..50) {
                val randomDigit = random.nextInt(10)
                stringBuilder.append(randomDigit)
            }
            if (selectedImageUri!=null) {
                progressDialog.show()
                val fileRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")
                fileRef.putFile(selectedImageUri!!)
                    .addOnSuccessListener {

                        fileRef.downloadUrl.addOnSuccessListener { uri ->
                            val activityObj: Activity? = this.activity
                            val user = User(firstName, lastName, username, password, phoneNumber ,uri.toString())
                            if (user.username != null) {
                                val databaseUser = FirebaseDatabase.getInstance("https://speedcamalert-3461b-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")
                                databaseUser.child(username).get().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val dataSnapshot = task.result
                                        if (dataSnapshot.exists()) {
                                            Toast.makeText(activityObj, "Vec postoji nalog sa tim usernameom", Toast.LENGTH_LONG).show()
                                        }
                                        else
                                        {
                                            databaseUser?.child(user.username)?.setValue(user)
                                                ?.addOnSuccessListener {
                                                    Navigation.findNavController(binding.root).navigate(R.id.action_registerFragment_to_loginFragment)
                                                    Toast.makeText(activityObj, "Uspesno registrovan korisnik", Toast.LENGTH_LONG).show()
                                                }
                                                ?.addOnFailureListener {
                                                    Toast.makeText(activityObj, "Bezuspesno registrovanje", Toast.LENGTH_LONG).show()
                                                }
                                        }
                                    }
                                }
                            } else {
                                val activityObj: Activity? = this.activity
                                Toast.makeText(activityObj, "Unesite sve podatke", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    .addOnProgressListener { taskSnapshot ->
                        val percent = ((100 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount).toInt()
                        progressDialog.progress = percent
                        progressDialog.setMessage("Registrovanje: $percent %")
                    }
                    .addOnFailureListener {
                        val activityObj: Activity? = this.activity
                        Toast.makeText(activityObj, "Doslo je do greske prilikom uploadovanja slike", Toast.LENGTH_LONG).show()
                    }
                    .addOnCompleteListener { task ->
                        progressDialog.dismiss()
                    }
            }

        }
    }

    private fun otvoriGaleriju()
    {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)

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


    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }
}