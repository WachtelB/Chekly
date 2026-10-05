package com.wachtel.chekly.model

data class Challenge(
    val category: ChallengeCategory,
    val limitRub: Int,
    val days: Int,
)