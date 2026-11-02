package com.nordix.chekly

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.nordix.chekly.challenge.Challenge
import com.nordix.chekly.challenge.ChallengeListScreen
import com.nordix.chekly.challenge.shareChallenge
import com.nordix.chekly.main.BottomBar
import com.nordix.chekly.main.MainScreen
import com.nordix.chekly.main.MonthPickerBottomSheet
import com.nordix.chekly.main.sampleReceipts
import com.nordix.chekly.ui.theme.CheklyTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

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
                var isInitialLoading by rememberSaveable { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    if (isInitialLoading) {
                        delay(1500.milliseconds)
                        isInitialLoading = false
                    }
                }

                val currentReceipts = if (selectedMonth.lowercase() == "октябрь" && selectedYear == 2026) {
                    sampleReceipts
                } else {
                    emptyList()
                }

                if (currentTab == 4) {
                    Scaffold(
                        bottomBar = {
                            BottomBar(
                                currentTab = currentTab,
                                onTabSelected = { currentTab = it }
                            )
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            ChallengeListScreen(
                                challenges = sampleChallenges,
                                onShare = { challenge -> shareChallenge(this@MainActivity, challenge) }
                            )
                        }
                    }
                } else {
                    MainScreen(
                        receipts = currentReceipts,
                        selectedMonth = selectedMonth,
                        selectedYear = selectedYear,
                        currentTab = currentTab,
                        isInitialLoading = isInitialLoading,
                        onTabSelected = { currentTab = it },
                        onMonthClick = { isMonthSheetOpen = true },
                        onSeeAllClick = { currentTab = 1 },
                        onAddReceiptClick = { currentTab = 2 },
                        onProfileClick = { currentTab = 4 }
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