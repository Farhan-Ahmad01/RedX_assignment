package com.example.clickretina_assignment.api

data class User(
    val avatar: String,
    val location: Location,
    val name: String,
    val social: Social,
    val statistics: Statistics,
    val username: String
)