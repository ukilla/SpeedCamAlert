package com.example.speedcamalert.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.speedcamalert.R
import com.example.speedcamalert.classes.Patrol
import com.example.speedcamalert.classes.User
import com.example.speedcamalert.databinding.FragmentHomeBinding
import com.example.speedcamalert.viewmodels.LoggedUserViewModel
import com.example.speedcamalert.viewmodels.PatrolViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.FirebaseDatabase

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var map: GoogleMap? = null
    private var isMapReady = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var location: MutableLiveData<Location>
    private  val patrolViewModel: PatrolViewModel by activityViewModels()
    private val loggedUserViewModel: LoggedUserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences =
            requireContext().getSharedPreferences("SpeedCamAlert", Context.MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("username", "")
        val databaseUser =
            FirebaseDatabase.getInstance("https://speedcamalert-3461b-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")
        databaseUser.child(savedUsername!!).get().addOnCompleteListener { task ->
            val dataSnapshot = task.result
            val firstName = dataSnapshot.child("firstName").getValue(String::class.java) ?: ""
            val lastName = dataSnapshot.child("lastName").getValue(String::class.java) ?: ""
            val imageUrl = dataSnapshot.child("imageURL").getValue(String::class.java) ?: ""
            val username = dataSnapshot.child("username").getValue(String::class.java) ?: ""
            val phoneNumber = dataSnapshot.child("phoneNumber").getValue(String::class.java) ?: ""
            val points = dataSnapshot.child("points").getValue(Int::class.java) ?: 0
            val password = dataSnapshot.child("password").getValue(String::class.java) ?: ""

            if (firstName != null && lastName != null && imageUrl != null && username != null
                && phoneNumber != null && points != null && password != null
            ) {
                // All values are not null, create the User object
                val userr = User(
                    firstName,
                    lastName,
                    username,
                    password,
                    phoneNumber,
                    imageUrl,
                    points
                )
                loggedUserViewModel.user = userr
            } else {
                Log.e("Firebase", "Some values are null.")
            }
        }
        //usersViewModel.getUsers()
        patrolViewModel.fetchPatrols()


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.fabAdd.setOnClickListener{
            this.findNavController().navigate(R.id.action_homeFragment_to_addPatrolFragment)
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        location = MutableLiveData()
        mapFragment!!.getMapAsync { mMap ->
            map = mMap
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            mMap.clear()
            mMap.uiSettings.isZoomControlsEnabled = true
            mMap.uiSettings.isCompassEnabled = true

            patrolViewModel.patrols.observe(viewLifecycleOwner, Observer { patrols ->
                Log.d("Home",patrols.size.toString())
                mMap.clear()
                for (patrol in patrols) {
                    addMarkerToMap(patrol)
                }
            })

            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    1001
                )
                return@getMapAsync
            }
            mMap.isMyLocationEnabled = true
            mMap.setOnInfoWindowClickListener(this)
            fusedLocationClient.lastLocation.addOnCompleteListener { location ->
                if (location.result != null) {
                    lastLocation = location.result
                    val currentLatLong = LatLng(location.result.latitude, location.result.longitude)
                    loggedUserViewModel.location = currentLatLong
                    val googlePlex = CameraPosition.builder()
                        .target(currentLatLong)
                        .zoom(15f)
                        .bearing(0f)
                        .tilt(0f)
                        .build()

                    mMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(googlePlex),
                        1000,
                        null
                    )

                }
            }.addOnFailureListener {
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item=menu.findItem(R.id.action_show_map)
        item.isVisible=false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_logout ->{
                val sharedPreferences =
                    requireContext().getSharedPreferences("SpeedCamAlert", Context.MODE_PRIVATE)
                sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
                this.findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                true
            }
            R.id.action_show_profile-> {
                this.findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                true
            }
//            R.id.action_show_scoreboard->{
//                this.findNavController().navigate(R.id.action_homeFragment_to_leaderboardFragment)
//                true
//            }
//            R.id.action_list->{
//                this.findNavController().navigate(R.id.action_homeFragment_to_strayListFragment)
//                true
//            }
            else->super.onContextItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Toast.makeText(this.activity, "Map is ready.", Toast.LENGTH_SHORT).show()
        map = googleMap
        isMapReady = true

    }

    override fun onInfoWindowClick(marker: Marker)
    {
        patrolViewModel.patrol=marker.tag as Patrol

        val dialogFragment = PatrolInfoFragment()
        dialogFragment.show(parentFragmentManager, "PatrolInfoDialog")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
    }

    private fun addMarkerToMap(patrol: Patrol) {
        val latLng = LatLng(patrol.latitude, patrol.longitude)
        val markerIcon: BitmapDescriptor = if (patrol.type == "Patrol") {
            BitmapDescriptorFactory.fromResource(R.drawable.car_icon)
        } else {
            BitmapDescriptorFactory.fromResource(R.drawable.cam_icon)
        }
        val markerOptions = MarkerOptions()
            .position(latLng)
            .title(patrol.name)
            .icon(markerIcon)

        val marker=map?.addMarker(markerOptions)
        marker?.tag = patrol
    }

    companion object {

    }

}
