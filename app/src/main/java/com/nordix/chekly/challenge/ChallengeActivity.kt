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
private const val KEY_CATEGORY = "challenge_category"
private const val KEY_LIMIT = "challenge_limit"
private const val KEY_DAYS = "challenge_days"
private const val KEY_ACCEPTED = "challenge_accepted"
class ChallengeActivity : LoggedActivity() {

    private val challenge = mutableStateOf<Challenge?>(null)
    private val accepted = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_CATEGORY)) {
            restoreState(savedInstanceState)
        } else {
            showLink(intent)
        }

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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent: ${intent.data}")
        setIntent(intent)
        showLink(intent)
    }

    private fun restoreState(saved: Bundle) {
        challenge.value = Challenge(
            category = saved.getString(KEY_CATEGORY) ?: "",
            limitRub = saved.getLong(KEY_LIMIT),
            days = saved.getInt(KEY_DAYS),
        )
        accepted.value = saved.getBoolean(KEY_ACCEPTED)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        challenge.value?.let { current ->
            outState.putString(KEY_CATEGORY, current.category)
            outState.putLong(KEY_LIMIT, current.limitRub)
            outState.putInt(KEY_DAYS, current.days)
            outState.putBoolean(KEY_ACCEPTED, accepted.value)
        }
    }
}