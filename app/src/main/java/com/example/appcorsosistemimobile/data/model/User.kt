package com.example.appcorsosistemimobile.data.model

data class User(
    val email: String = "",
    val name: String = "",
    val surname: String = "",
    val favouriteDiveSite: List<String> = emptyList()
)