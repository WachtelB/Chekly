package com.nordix.chekly.challenge

import android.net.Uri

enum class ChallengeLinkError { NOT_A_CHALLENGE_LINK, INVALID_DATA }

sealed interface ChallengeParseResult {
    data class Success(val challenge: Challenge) : ChallengeParseResult
    data class Failure(val error: ChallengeLinkError) : ChallengeParseResult
}

object ChallengeLink {
    const val SCHEME = "chekly"
    const val HOST = "challenge"
    private const val KEY_CATEGORY = "category"
    private const val KEY_LIMIT = "limit"
    private const val KEY_DAYS = "days"

    fun build(challenge: Challenge): Uri = Uri.Builder()
        .scheme(SCHEME)
        .authority(HOST)
        .appendQueryParameter(KEY_CATEGORY, challenge.category)
        .appendQueryParameter(KEY_LIMIT, challenge.limitRub.toString())
        .appendQueryParameter(KEY_DAYS, challenge.days.toString())
        .build()

    fun parse(uri: Uri?): ChallengeParseResult {
        if (uri == null || uri.scheme != SCHEME || uri.host != HOST) {
            return ChallengeParseResult.Failure(ChallengeLinkError.NOT_A_CHALLENGE_LINK)
        }
        val challenge = Challenge.createOrNull(
            category = uri.getQueryParameter(KEY_CATEGORY),
            limitRub = uri.getQueryParameter(KEY_LIMIT)?.toLongOrNull(),
            days = uri.getQueryParameter(KEY_DAYS)?.toIntOrNull(),
        )
        return if (challenge != null) {
            ChallengeParseResult.Success(challenge)
        } else {
            ChallengeParseResult.Failure(ChallengeLinkError.INVALID_DATA)
        }
    }
}