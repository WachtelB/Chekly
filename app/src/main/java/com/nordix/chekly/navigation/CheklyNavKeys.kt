package com.nordix.chekly.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object HomeDashboardKey : NavKey

@Serializable
data class HomeReceiptPreviewKey(val receiptId: String) : NavKey

@Serializable
data object HistoryListKey : NavKey

@Serializable
data class ReceiptDetailKey(val receiptId: String) : NavKey

@Serializable
data class ReceiptEditKey(val receiptId: String) : NavKey

fun NavBackStack<NavKey>.selectHistoryReceipt(receiptId: String) {
    while (size > 1 && last() is ReceiptDetailKey) {
        removeLastOrNull()
    }
    add(ReceiptDetailKey(receiptId))
}

fun NavBackStack<NavKey>.selectHomeReceiptPreview(receiptId: String) {
    while (size > 1 && last() is HomeReceiptPreviewKey) {
        removeLastOrNull()
    }
    add(HomeReceiptPreviewKey(receiptId))
}