package com.example.speedcamalert.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.speedcamalert.classes.User
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoggedUserViewModel : ViewModel() {
    private val database= Firebase.database.reference
    private val _user = MutableLiveData<User?>(null)
    private val _location = MutableLiveData<LatLng?>(null)
    var user
        get() = _user.value
        set(value) {
            _user.value = value
        }

    var location
        get() = _location.value
        set(value) {
            _location.value = value
        }

    fun addPointsForComment()
    {
        database.child("Users").child(user?.username!!).child("points").setValue(user?.points!!+1)
    }
}