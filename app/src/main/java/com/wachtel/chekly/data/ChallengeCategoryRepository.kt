package com.wachtel.chekly.data

import com.wachtel.chekly.model.ChallengeCategory

interface ChallengeCategoryRepository {
    fun findByKey(key: String): ChallengeCategory?
    fun all(): List<ChallengeCategory>
}

class InMemoryChallengeCategoryRepository : ChallengeCategoryRepository {

    private val categories = listOf(
        ChallengeCategory(key = "fastfood", title = "Фастфуд"),
        ChallengeCategory(key = "taxi", title = "Такси"),
        ChallengeCategory(key = "coffee", title = "Кофе"),
    )

    override fun findByKey(key: String): ChallengeCategory? =
        categories.find { it.key == key }

    override fun all(): List<ChallengeCategory> = categories
}