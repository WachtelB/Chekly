package com.nordix.chekly.main

import androidx.compose.runtime.Immutable

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
    data class Error(val message: String) : MainResult
}

@Immutable
data class MainUiState(
    val searchQuery: String = "",
    val selectedMonthIndex: Int = 9,
    val selectedYear: Int = 2026,
    val isMonthPickerVisible: Boolean = false,
    val result: MainResult = MainResult.Loading,
    val userMessage: String? = null
) {
    val selectedMonth: String
        get() = allMonths.getOrElse(selectedMonthIndex) { allMonths.first() }

    val periodText: String
        get() = "$selectedMonth $selectedYear"
}