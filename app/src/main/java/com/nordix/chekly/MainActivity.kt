package com.nordix.chekly

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.nordix.chekly.challenge.Challenge
import com.nordix.chekly.challenge.ChallengeListScreen
import com.nordix.chekly.challenge.shareChallenge
import com.nordix.chekly.main.CheklyBottomBar
import com.nordix.chekly.main.MainScreenContainer
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
                var currentTab by rememberSaveable { mutableIntStateOf(0) }

                Scaffold(
                    bottomBar = {
                        CheklyBottomBar(
                            currentTab = currentTab,
                            onTabSelected = { currentTab = it }
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        if (currentTab == 4) {
                            ChallengeListScreen(
                                challenges = sampleChallenges,
                                onShare = { challenge -> shareChallenge(this@MainActivity, challenge) }
                            )
                        } else {
                            MainScreenContainer(
                                onSeeAllClick = { currentTab = 1 },
                                onProfileClick = { currentTab = 4 },
                                onAddReceiptClick = { currentTab = 2 }
                            )
                        }
                    }
                }
            }
        }
    }
}