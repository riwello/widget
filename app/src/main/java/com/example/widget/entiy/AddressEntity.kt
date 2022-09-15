package com.example.widget.entiy

data class AddressEntity(
    val value: Int,
    val label: String,
    val children: ArrayList<AddressEntity>
)