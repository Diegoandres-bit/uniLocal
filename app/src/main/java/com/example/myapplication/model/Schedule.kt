package com.example.myapplication.model

import java.time.DayOfWeek
import java.time.LocalTime

data class Schedule (
    val day: DayOfWeek,
    val open: LocalTime,
    val close: LocalTime
){

    fun toDisplayString(): String {
        return "${day.name} $open - $close"
    }

}