package com.nordix.chekly

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.nordix.chekly.challenge.Challenge
import com.nordix.chekly.challenge.ChallengeListScreen
import com.nordix.chekly.challenge.shareChallenge
import com.nordix.chekly.main.MainScreen
import com.nordix.chekly.main.MonthPickerBottomSheet
import com.nordix.chekly.main.sampleReceipts
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

                var selectedMonth by rememberSaveable { mutableStateOf("Октябрь") }
                var selectedYear by rememberSaveable { mutableIntStateOf(2026) }
                var isMonthSheetOpen by rememberSaveable { mutableStateOf(false) }

                val currentReceipts = if (selectedMonth.lowercase() == "октябрь" && selectedYear == 2026) {
                    sampleReceipts
                } else {
                    emptyList()
                }

                if (currentTab == 0) {
                    MainScreen(
                        receipts = currentReceipts,
                        selectedMonth = selectedMonth,
                        selectedYear = selectedYear,
                        currentTab = currentTab,
                        onTabSelected = { currentTab = it },
                        onMonthClick = { isMonthSheetOpen = true }
                    )
                } else {
                    ChallengeListScreen(
                        challenges = sampleChallenges,
                        onShare = { challenge -> shareChallenge(this@MainActivity, challenge) }
                    )
                }

                if (isMonthSheetOpen) {
                    MonthPickerBottomSheet(
                        currentMonth = selectedMonth,
                        currentYear = selectedYear,
                        onDismiss = { isMonthSheetOpen = false },
                        onApply = { chosenMonth, chosenYear ->
                            selectedMonth = chosenMonth
                            selectedYear = chosenYear
                            isMonthSheetOpen = false
                        }
                    )
                }
            }
        }
    }
}