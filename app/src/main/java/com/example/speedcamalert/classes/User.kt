package com.example.speedcamalert.classes

data class User(
    val firstName: String,
    val lastName: String,
    val username: String,
    val password: String,
    val phoneNumber: String,
    var imageUrl:String,
    var points: Int=0
)
{
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        0,
    )
}
