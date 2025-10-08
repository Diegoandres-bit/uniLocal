package com.example.myapplication.model

import java.time.LocalDateTime

data class Place(
    val id: String,
    val title: String,
    val description: String,
    val address: String,
    val city: City,
    val location: Location,
    val images: List<String>,
    val phoneNumber: String,
    val type: PlaceType,
    val schedules: List<Schedule>,
    val createdByUserId: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val status: ReviewStatus = ReviewStatus.PENDING,
    val distanceKm: Double? = null,
    val puntuation: Double? = null


){

    fun isOpen():Boolean{
        val now = LocalDateTime.now()

        if(schedules.isEmpty()){
            return false
        }

        val schedule = schedules.find { it.day == now.dayOfWeek }

        if(schedule == null){
            return false
        }

        return now.toLocalTime().isAfter(schedule.open) && now.toLocalTime().isBefore(schedule.close)
    }

    fun hourClosed(): String{
        val now = LocalDateTime.now()

        if(schedules.isEmpty()){
            return ""
        }

        val schedule = schedules.find { it.day == now.dayOfWeek }

        if(schedule == null){
            return ""
        }

        return schedule.close.toString()
    }


}