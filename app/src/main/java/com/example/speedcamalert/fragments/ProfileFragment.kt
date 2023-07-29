package com.example.speedcamalert.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.speedcamalert.R
import com.example.speedcamalert.classes.User
import com.example.speedcamalert.databinding.FragmentProfileBinding
import com.example.speedcamalert.viewmodels.LoggedUserViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val loggedUserViewModel: LoggedUserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
        var realUrl=loggedUserViewModel.user?.imageUrl
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null&&user.username==loggedUserViewModel.user?.username) {
                        realUrl = user.imageUrl

                        val profileImage = view.findViewById<ImageView>(R.id.profileImage)
                        Glide.with(requireContext())
                            .load(realUrl)
                            .placeholder(R.drawable.avatar)
                            .into(profileImage)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseData", "Greška pri čitanju podataka: ${databaseError.message}")
            }
        })
        val usernameTextView = view.findViewById<TextView>(R.id.usernameTextView)
        usernameTextView.text = loggedUserViewModel.user?.username

        val phoneTextView = view.findViewById<TextView>(R.id.phoneTextView)
        phoneTextView.text = loggedUserViewModel.user?.phoneNumber

        val nameTextView = view.findViewById<TextView>(R.id.nameTextView)
        nameTextView.text = loggedUserViewModel.user?.firstName

        val surnameTextView = view.findViewById<TextView>(R.id.surnameTextView)
        surnameTextView.text=loggedUserViewModel.user?.lastName

        val pointsTextView = view.findViewById<TextView>(R.id.pointsTextView)
        pointsTextView.text="Broj poena: "+loggedUserViewModel.user?.points
    }

    companion object {
    }

}