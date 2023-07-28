package com.example.speedcamalert.classes

import java.util.Date

data class Patrol(
    var id: String= "",
    var name: String="",
    var publisher: String="",
    var description: String="",
    var type: String="",
    var latitude: Double=0.0,
    var longitude: Double=0.0,
    var date: Date,
    var reviews:HashMap<String, Review> = HashMap(),
    var photo: String=""

) {
    constructor() : this(
        "", "", "", "","", 0.0, 0.0, Date(), HashMap(),""
    )

//    public fun getPublisher(): String {
//        return publisher
//    }
}
