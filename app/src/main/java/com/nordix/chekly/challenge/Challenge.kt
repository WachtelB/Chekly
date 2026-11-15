package com.nordix.chekly.challenge

import androidx.compose.runtime.Immutable
import java.io.Serializable

val sampleChallenges = listOf(
    Challenge(category = "Еда", limitRub = 5000, days = 7),
    Challenge(category = "Кафе", limitRub = 2000, days = 14),
    Challenge(category = "Транспорт", limitRub = 3000, days = 30),
)

@Immutable
data class Challenge(
    val category: String,
    val limitRub: Long,
    val days: Int,
) : Serializable {
    companion object {
        const val MAX_CATEGORY_LENGTH = 40
        const val MAX_LIMIT_RUB = 10_000_000L
        const val MAX_DAYS = 365

        fun createOrNull(category: String?, limitRub: Long?, days: Int?): Challenge? {
            val cleanCategory = category?.trim()
            if (cleanCategory.isNullOrEmpty() || cleanCategory.length > MAX_CATEGORY_LENGTH) return null
            if (limitRub == null || limitRub !in 1L..MAX_LIMIT_RUB) return null
            if (days == null || days !in 1..MAX_DAYS) return null
            return Challenge(cleanCategory, limitRub, days)
        }
    }
}