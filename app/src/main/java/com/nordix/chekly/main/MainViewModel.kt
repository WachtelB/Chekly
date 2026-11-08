package com.nordix.chekly.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private const val KEY_QUERY = "main_search_query"
private const val KEY_MONTH_INDEX = "main_month_index"
private const val KEY_YEAR = "main_year"

class MainViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialQuery = savedStateHandle.get<String>(KEY_QUERY) ?: ""
    private val initialMonth = savedStateHandle.get<Int>(KEY_MONTH_INDEX) ?: 9
    private val initialYear = savedStateHandle.get<Int>(KEY_YEAR) ?: 2026

    private val _uiState = MutableStateFlow(
        MainUiState(
            searchQuery = initialQuery,
            selectedMonthIndex = initialMonth,
            selectedYear = initialYear,
            result = buildResult(initialMonth, initialYear, initialQuery)
        )
    )
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun onAction(action: MainAction) {
        when (action) {
            is MainAction.QueryChanged -> updateQuery(action.query)
            MainAction.PrevMonthClick -> shiftMonth(-1)
            MainAction.NextMonthClick -> shiftMonth(1)
            MainAction.OpenMonthPicker -> {
                _uiState.update { it.copy(isMonthPickerVisible = true) }
            }
            MainAction.DismissMonthPicker -> {
                _uiState.update { it.copy(isMonthPickerVisible = false) }
            }
            is MainAction.SelectPeriod -> {
                val monthIdx = allMonths.indexOf(action.monthName).takeIf { it >= 0 } ?: 0
                val year = action.year.coerceIn(1970, 2100)
                applyPeriod(
                    monthIndex = monthIdx,
                    year = year,
                    message = "Выбран период: ${allMonths[monthIdx]} $year"
                )
            }
            is MainAction.ReceiptClicked -> {
                _uiState.update { it.copy(userMessage = "Чек: ${action.shopName}") }
            }
            MainAction.Retry -> {
                val current = _uiState.value
                _uiState.update {
                    it.copy(
                        result = MainResult.Empty,
                        userMessage = "Данные за ${current.periodText} обновлены"
                    )
                }
            }
            MainAction.MessageShown -> {
                _uiState.update { it.copy(userMessage = null) }
            }
        }
    }

    private fun updateQuery(newQuery: String) {
        savedStateHandle[KEY_QUERY] = newQuery
        _uiState.update { current ->
            current.copy(
                searchQuery = newQuery,
                result = buildResult(current.selectedMonthIndex, current.selectedYear, newQuery)
            )
        }
    }

    private fun shiftMonth(delta: Int) {
        val current = _uiState.value
        var month = current.selectedMonthIndex + delta
        var year = current.selectedYear
        if (month < 0) {
            month = allMonths.lastIndex
            year--
        } else if (month > allMonths.lastIndex) {
            month = 0
            year++
        }
        applyPeriod(monthIndex = month, year = year)
    }

    private fun applyPeriod(monthIndex: Int, year: Int, message: String? = null) {
        savedStateHandle[KEY_MONTH_INDEX] = monthIndex
        savedStateHandle[KEY_YEAR] = year
        _uiState.update { current ->
            current.copy(
                selectedMonthIndex = monthIndex,
                selectedYear = year,
                isMonthPickerVisible = false,
                result = buildResult(monthIndex, year, current.searchQuery),
                userMessage = message ?: current.userMessage
            )
        }
    }

    private fun buildResult(monthIndex: Int, year: Int, query: String): MainResult {
        return when {
            monthIndex == 8 && year == 2026 -> MainResult.Loading
            monthIndex == 7 && year == 2026 -> MainResult.Error(
                message = "Ошибка синхронизации за Август 2026"
            )
            monthIndex == 9 && year == 2026 -> {
                val filtered = if (query.isBlank()) {
                    sampleReceipts
                } else {
                    sampleReceipts.filter {
                        it.shopName.contains(query, ignoreCase = true) ||
                                it.category.contains(query, ignoreCase = true)
                    }
                }
                if (filtered.isEmpty()) {
                    MainResult.Empty
                } else {
                    MainResult.Content(
                        receipts = filtered,
                        categories = calculateCategoryExpenses(filtered)
                    )
                }
            }
            else -> MainResult.Empty
        }
    }
}