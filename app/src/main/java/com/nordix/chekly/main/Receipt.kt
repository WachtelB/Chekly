package com.nordix.chekly.main

data class Receipt(
    val id: String,
    val shopName: String,
    val category: String,
    val amountRub: Long,
    val dateText: String
)