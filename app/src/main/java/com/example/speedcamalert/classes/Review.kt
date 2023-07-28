package com.example.speedcamalert.classes

data class Review(
    val rating: Int,
    val comment: String,
    val publisher: String
){
    constructor():this(0,"","")
}





