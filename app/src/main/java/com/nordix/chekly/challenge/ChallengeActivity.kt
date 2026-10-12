package com.nordix.chekly.challenge

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import com.nordix.chekly.LoggedActivity
import com.nordix.chekly.ui.theme.CheklyTheme

private const val TAG = "ChallengeActivity"
class ChallengeActivity : LoggedActivity() {

    private val challenge = mutableStateOf<Challenge?>(null)
    private val accepted = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        showLink(intent)

        setContent {
            CheklyTheme {
                ChallengeScreen(
                    challenge = challenge.value,
                    accepted = accepted.value,
                    onAccept = { accepted.value = true },
                    onClose = { finish() },
                )
            }
        }
    }

    private fun showLink(linkIntent: Intent) {
        val result = ChallengeLink.parse(linkIntent.data)
        challenge.value = when (result) {
            is ChallengeParseResult.Success -> result.challenge
            is ChallengeParseResult.Failure -> {
                Log.w(TAG, "Ссылка отклонена: ${result.error}, data=${linkIntent.data}")
                null
            }
        }
        accepted.value = false
    }
}