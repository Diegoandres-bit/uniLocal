package com.example.myapplication.model

data class User(
    val id: String,
    val name: String,
    val username: String,
    val role: Role,
    val city: City,
    val email: String,
    val password: String
)