package com.example.speedcamalert.classes

import java.util.Date

data class Review(
    val active:Boolean,
    val comment: String,
    val publisher: String,
    val date:Date
){
    constructor():this(true,"","",Date())

    fun copy(): Review {
        return Review(active, comment, publisher, date)
    }

}





