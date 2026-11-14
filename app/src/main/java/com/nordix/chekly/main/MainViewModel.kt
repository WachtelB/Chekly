package com.nordix.chekly.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nordix.chekly.main.components.allMonths
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private const val KEY_TAB = "saved_tab_name"
private const val KEY_MONTH_INDEX = "saved_month_index"
private const val KEY_YEAR = "saved_year"

class MainViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialTab = savedStateHandle.get<String>(KEY_TAB)
        ?.let { runCatching { AppTab.valueOf(it) }.getOrNull() }
        ?: AppTab.HOME
    private val initialMonth = savedStateHandle.get<Int>(KEY_MONTH_INDEX) ?: 9
    private val initialYear = savedStateHandle.get<Int>(KEY_YEAR) ?: 2026

    private val _uiState = MutableStateFlow(
        MainUiState(
            currentTab = initialTab,
            selectedMonthIndex = initialMonth,
            selectedYear = initialYear,
            result = evaluateResult(initialMonth, initialYear)
        )
    )
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun onAction(action: MainAction) {
        when (action) {
            is MainAction.TabSelected -> selectTab(action.tab)
            MainAction.SeeAllClick -> selectTab(AppTab.HISTORY)
            MainAction.AddReceiptClick -> selectTab(AppTab.ADD)
            MainAction.ProfileClick -> selectTab(AppTab.CHALLENGES)

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
                savedStateHandle[KEY_TAB] = AppTab.HOME.name
                savedStateHandle[KEY_MONTH_INDEX] = 9
                savedStateHandle[KEY_YEAR] = 2026
                _uiState.update { current ->
                    current.copy(
                        currentTab = AppTab.HOME,
                        selectedMonthIndex = 9,
                        selectedYear = 2026,
                        result = evaluateResult(monthIndex = 9, year = 2026),
                        userMessage = "Данные успешно обновлены"
                    )
                }
            }
            MainAction.MessageShown -> {
                _uiState.update { it.copy(userMessage = null) }
            }
        }
    }

    private fun selectTab(tab: AppTab) {
        savedStateHandle[KEY_TAB] = tab.name
        _uiState.update { current ->
            current.copy(currentTab = tab)
        }
    }

    private fun shiftMonth(delta: Int) {
        val current = _uiState.value
        val totalMonths = current.selectedYear * 12 + current.selectedMonthIndex + delta
        val newYear = totalMonths / 12
        val newMonth = totalMonths.mod(12)
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
                result = evaluateResult(monthIndex, year),
                userMessage = userMessage ?: current.userMessage
            )
        }
    }

    private fun evaluateResult(monthIndex: Int, year: Int): MainResult {
        return when {
            monthIndex == 8 && year == 2026 -> MainResult.Loading
            monthIndex == 7 && year == 2026 -> MainResult.Error(
                message = "Не удалось загрузить чеки за август 2026."
            )
            monthIndex == 9 && year == 2026 -> {
                val receipts = sampleReceipts
                val categories = calculateCategoryExpenses(receipts)
                MainResult.Content(receipts = receipts, categories = categories)
            }
            else -> MainResult.Empty
        }
    }
}