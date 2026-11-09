package com.nordix.chekly.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private const val KEY_TAB = "saved_tab_index"
private const val KEY_MONTH_INDEX = "saved_month_index"
private const val KEY_YEAR = "saved_year"

class MainViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialTab = savedStateHandle.get<Int>(KEY_TAB) ?: 0
    private val initialMonth = savedStateHandle.get<Int>(KEY_MONTH_INDEX) ?: 9
    private val initialYear = savedStateHandle.get<Int>(KEY_YEAR) ?: 2026

    private val _uiState = MutableStateFlow(
        MainUiState(
            currentTab = initialTab,
            selectedMonthIndex = initialMonth,
            selectedYear = initialYear,
            result = evaluateResult(initialTab, initialMonth, initialYear)
        )
    )
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun onAction(action: MainAction) {
        when (action) {
            is MainAction.TabSelected -> selectTab(action.tabIndex)
            MainAction.SeeAllClick -> selectTab(1)
            MainAction.AddReceiptClick -> selectTab(2)
            MainAction.ProfileClick -> selectTab(4)

            MainAction.PrevMonthClick -> shiftMonth(delta = -1)
            MainAction.NextMonthClick -> shiftMonth(delta = 1)

            MainAction.OpenMonthPicker -> {
                _uiState.update { it.copy(isMonthPickerVisible = true) }
            }
            MainAction.DismissMonthPicker -> {
                _uiState.update { it.copy(isMonthPickerVisible = false) }
            }
            is MainAction.SelectPeriod -> {
                val monthIndex = allMonths.indexOf(action.monthName).takeIf { it >= 0 } ?: 0
                val year = action.year.coerceIn(1970, 2100)
                updatePeriod(
                    monthIndex = monthIndex,
                    year = year,
                    userMessage = "Выбран период: ${allMonths[monthIndex]} $year"
                )
            }
            is MainAction.ReceiptClicked -> {
                _uiState.update {
                    it.copy(userMessage = "Чек из магазина «${action.shopName}»")
                }
            }
            MainAction.Retry -> {
                savedStateHandle[KEY_TAB] = 0
                _uiState.update { current ->
                    current.copy(
                        currentTab = 0,
                        result = evaluateResult(0, current.selectedMonthIndex, current.selectedYear),
                        userMessage = "Возврат на главный экран"
                    )
                }
            }
            MainAction.MessageShown -> {
                _uiState.update { it.copy(userMessage = null) }
            }
        }
    }

    private fun selectTab(tabIndex: Int) {
        savedStateHandle[KEY_TAB] = tabIndex
        _uiState.update { current ->
            current.copy(
                currentTab = tabIndex,
                result = evaluateResult(tabIndex, current.selectedMonthIndex, current.selectedYear)
            )
        }
    }

    private fun shiftMonth(delta: Int) {
        val current = _uiState.value
        var newMonth = current.selectedMonthIndex + delta
        var newYear = current.selectedYear

        if (newMonth < 0) {
            newMonth = allMonths.lastIndex
            newYear--
        } else if (newMonth > allMonths.lastIndex) {
            newMonth = 0
            newYear++
        }
        updatePeriod(monthIndex = newMonth, year = newYear)
    }

    private fun updatePeriod(
        monthIndex: Int,
        year: Int,
        userMessage: String? = null
    ) {
        savedStateHandle[KEY_MONTH_INDEX] = monthIndex
        savedStateHandle[KEY_YEAR] = year

        _uiState.update { current ->
            current.copy(
                selectedMonthIndex = monthIndex,
                selectedYear = year,
                isMonthPickerVisible = false,
                result = evaluateResult(current.currentTab, monthIndex, year),
                userMessage = userMessage ?: current.userMessage
            )
        }
    }

    private fun evaluateResult(tabIndex: Int, monthIndex: Int, year: Int): MainResult {
        return when {
            tabIndex == 1 || (monthIndex == 8 && year == 2026) -> {
                MainResult.Loading
            }
            tabIndex in 2..3 -> {
                MainResult.Error(
                    message = "Не удалось загрузить экран. Попробуйте вернуться на главную."
                )
            }
            monthIndex == 9 && year == 2026 -> {
                val receipts = sampleReceipts
                val categories = calculateCategoryExpenses(receipts)
                MainResult.Content(receipts = receipts, categories = categories)
            }
            else -> MainResult.Empty
        }
    }
}