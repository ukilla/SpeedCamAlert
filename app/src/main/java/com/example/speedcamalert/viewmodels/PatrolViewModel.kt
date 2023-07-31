package com.example.speedcamalert.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.speedcamalert.classes.Patrol
import com.example.speedcamalert.classes.Review
import com.example.speedcamalert.classes.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.Date
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class PatrolViewModel : ViewModel(){
    private val database= Firebase.database.reference
    private val storageRef = FirebaseStorage.getInstance().reference

    private val _patrol= MutableLiveData<Patrol?>(null)
    private val _patrols=MutableLiveData<List<Patrol>>(emptyList())
    private var patrolList: List<Patrol> = emptyList()

    var patrol
        get() = _patrol.value
        set(value) { _patrol.value=value}

    val patrols: LiveData<List<Patrol>> get() = _patrols

    private fun getDistance(currentLat: Double, currentLon: Double, deviceLat: Double, deviceLon: Double): Double {
        val earthRadius = 6371000.0
        val currentLatRad = Math.toRadians(currentLat)
        val deviceLatRad = Math.toRadians(deviceLat)
        val deltaLat = Math.toRadians(deviceLat - currentLat)
        val deltaLon = Math.toRadians(deviceLon - currentLon)

        val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
                cos(currentLatRad) * cos(deviceLatRad) *
                sin(deltaLon / 2) * sin(deltaLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    fun addPatrol(patrol: Patrol, user: User)
    {
        database.child("Users").child(patrol.publisher).child("points").setValue(user.points+10)
    }

    fun fetchPatrols() {
        val patrolsRef = database.child("Patrols")

        patrolsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val patrolsList = mutableListOf<Patrol>()

                for (patrolSnapshot in snapshot.children) {
                    val patrol = patrolSnapshot.getValue(Patrol::class.java)
                    patrol?.let { patrolsList.add(it) }
                }

                patrolList = patrolsList
                _patrols.value = patrolsList
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun addReviewToPatrol( review: Review) {

        val patrolId = patrol?.id ?: return

        val reviewRef: DatabaseReference = database.child("Patrols").child(patrolId).child("reviews").push()
        reviewRef.setValue(review)

        patrol?.reviews?.put(reviewRef.key!!, review)
    }

    fun getReviewsForPatrol(): List<Review> {

        return patrol?.reviews!!.values.toList()?: emptyList()
    }

    fun filterPatrols(nameFilter: String?, typeFilter: String?, dateFilter: Date?, radiusFilter:Int?, userLat:Double?, userLon: Double?) {
        val filteredList = patrolList.filter { patrol ->
            var includePatrol = true
            if (nameFilter != null) {
                includePatrol = includePatrol && patrol.name.contains(nameFilter, ignoreCase = true)
            }
            if (typeFilter != null) {
                includePatrol = includePatrol && patrol.type == typeFilter
            }
            if (dateFilter != null) {
                includePatrol = includePatrol && patrol.date >= dateFilter
            }
            if(radiusFilter!= null)
            {
                includePatrol=includePatrol && getDistance(userLat!!,userLon!!,patrol.latitude!!,patrol.longitude!!)<radiusFilter
            }
            includePatrol
        }
        _patrols.value = filteredList
    }



}