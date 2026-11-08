package com.nordix.chekly

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.nordix.chekly.challenge.Challenge
import com.nordix.chekly.challenge.ChallengeListScreen
import com.nordix.chekly.challenge.shareChallenge
import com.nordix.chekly.main.CheklyBottomBar
import com.nordix.chekly.main.MainScreen
import com.nordix.chekly.main.MonthPickerBottomSheet
import com.nordix.chekly.main.allMonths
import com.nordix.chekly.main.calculateCategoryExpenses
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
                var currentMonthIndex by rememberSaveable { mutableIntStateOf(9) }
                var currentYear by rememberSaveable { mutableIntStateOf(2026) }
                var isMonthPickerVisible by rememberSaveable { mutableStateOf(false) }

                val currentMonthName = allMonths[currentMonthIndex]
                val currentPeriodText = "$currentMonthName $currentYear"

                val currentReceipts = if (currentMonthIndex == 9 && currentYear == 2026) {
                    sampleReceipts
                } else {
                    emptyList()
                }

                val categories = calculateCategoryExpenses(currentReceipts)
                val isLoading = (currentMonthIndex == 8 && currentYear == 2026) || currentTab == 1
                val isError = currentTab in 2..3

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
                            MainScreen(
                                receipts = currentReceipts,
                                categories = categories,
                                periodText = currentPeriodText,
                                isLoading = isLoading,
                                isError = isError,
                                onPrevMonthClick = {
                                    if (currentMonthIndex > 0) {
                                        currentMonthIndex--
                                    } else {
                                        currentMonthIndex = allMonths.lastIndex
                                        currentYear--
                                    }
                                },
                                onNextMonthClick = {
                                    if (currentMonthIndex < allMonths.lastIndex) {
                                        currentMonthIndex++
                                    } else {
                                        currentMonthIndex = 0
                                        currentYear++
                                    }
                                },
                                onPeriodClick = { isMonthPickerVisible = true },
                                onSeeAllClick = { currentTab = 1 },
                                onProfileClick = { currentTab = 4 },
                                onAddReceiptClick = { currentTab = 2 }
                            )
                        }

                        if (isMonthPickerVisible) {
                            MonthPickerBottomSheet(
                                currentMonth = currentMonthName,
                                currentYear = currentYear,
                                onDismiss = { isMonthPickerVisible = false },
                                onApply = { selectedMonth, selectedYear ->
                                    val newIndex = allMonths.indexOf(selectedMonth)
                                    if (newIndex != -1) {
                                        currentMonthIndex = newIndex
                                    }
                                    currentYear = selectedYear
                                    isMonthPickerVisible = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}