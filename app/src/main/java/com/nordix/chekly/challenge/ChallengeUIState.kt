package com.nordix.chekly.challenge

import androidx.compose.runtime.Immutable

@Immutable
sealed interface ChallengeResult {
    data object Loading : ChallengeResult

    @Immutable
    data class Content(
        val challenge: Challenge,
        val accepted: Boolean
    ) : ChallengeResult

    data object Empty : ChallengeResult

    @Immutable
    data class Error(val message: String) : ChallengeResult
}

@Immutable
data class ChallengeUiState(
    val note: String = "",
    val result: ChallengeResult = ChallengeResult.Loading,
    val userMessage: String? = null
)