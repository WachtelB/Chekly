package com.nordix.chekly

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nordix.chekly.challenge.Challenge
import com.nordix.chekly.challenge.ChallengeListScreen
import com.nordix.chekly.challenge.shareChallenge
import com.nordix.chekly.ui.theme.CheklyTheme

private val sampleChallenges = listOf(
    Challenge(category = "Еда", limitRub = 5000, days = 7),
    Challenge(category = "Кафе", limitRub = 2000, days = 14),
    Challenge(category = "Транспорт", limitRub = 3000, days = 30),
)

class MainActivity : LoggedActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CheklyTheme {
                ChallengeListScreen(
                    challenges = sampleChallenges,
                    onShare = { challenge -> shareChallenge(this@MainActivity, challenge) },
                )
            }
        }
    }
}