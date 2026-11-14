package com.nordix.chekly.main

import androidx.compose.runtime.Immutable
import com.nordix.chekly.challenge.Challenge
import com.nordix.chekly.challenge.sampleChallenges
import com.nordix.chekly.main.components.allMonths

@Immutable
sealed interface MainResult {
    data object Loading : MainResult

    @Immutable
    data class Content(
        val receipts: List<Receipt>,
        val categories: List<CategoryExpense>
    ) : MainResult

    data object Empty : MainResult

    @Immutable
    data class Error(
        val message: String
    ) : MainResult
}

@Immutable
data class MainUiState(
    val currentTab: AppTab = AppTab.HOME,
    val selectedMonthIndex: Int = 9,
    val selectedYear: Int = 2026,
    val isMonthPickerVisible: Boolean = false,
    val result: MainResult = MainResult.Loading,
    val challenges: List<Challenge> = sampleChallenges,
    val userMessage: String? = null
) {
    val selectedMonth: String
        get() = allMonths.getOrElse(selectedMonthIndex) { allMonths.first() }

    val periodText: String
        get() = "$selectedMonth $selectedYear"
}