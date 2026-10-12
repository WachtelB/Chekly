package com.nordix.chekly.challenge

import android.content.Context
import android.content.Intent
import com.nordix.chekly.R

fun shareChallenge(context: Context, challenge: Challenge) {
    val link = ChallengeLink.build(challenge)
    val text = context.getString(
        R.string.share_text,
        challenge.category,
        challenge.limitRub,
        challenge.days,
        link.toString(),
    )

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }

    context.startActivity(
        Intent.createChooser(sendIntent, context.getString(R.string.share_chooser_title))
    )
}