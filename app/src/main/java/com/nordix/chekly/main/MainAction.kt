package com.nordix.chekly.main

sealed interface MainAction {
    data class QueryChanged(val query: String) : MainAction
    data object PrevMonthClick : MainAction
    data object NextMonthClick : MainAction
    data object OpenMonthPicker : MainAction
    data object DismissMonthPicker : MainAction
    data class SelectPeriod(val monthName: String, val year: Int) : MainAction
    data class ReceiptClicked(val shopName: String) : MainAction
    data object Retry : MainAction
    data object MessageShown : MainAction
}