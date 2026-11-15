package com.nordix.chekly.data

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Immutable
data class ReceiptItem(
    val id: String,
    val name: String,
    val quantity: Int,
    val priceRub: Long
) {
    val totalRub: Long get() = quantity * priceRub
}

@Immutable
data class Receipt(
    val id: String,
    val shopName: String,
    val shopAddress: String,
    val category: String,
    val dateText: String,
    val monthIndex: Int = 9,
    val year: Int = 2026,
    val items: List<ReceiptItem> = emptyList()
) {
    val amountRub: Long
        get() = items.sumOf { it.totalRub }
}

object ReceiptRepository {
    private val _receipts = MutableStateFlow(sampleReceipts)
    val receipts: StateFlow<List<Receipt>> = _receipts.asStateFlow()

    fun getReceiptById(id: String): Receipt? {
        return _receipts.value.find { it.id == id }
    }

    fun updateReceipt(
        id: String,
        shopName: String,
        shopAddress: String,
        category: String,
        dateText: String,
        items: List<ReceiptItem>
    ) {
        _receipts.update { list ->
            list.map { receipt ->
                if (receipt.id == id) {
                    receipt.copy(
                        shopName = shopName.trim(),
                        shopAddress = shopAddress.trim(),
                        category = category.trim(),
                        dateText = dateText.trim(),
                        items = items
                    )
                } else {
                    receipt
                }
            }
        }
    }
}

val sampleReceipts = listOf(
    Receipt(
        id = "1",
        shopName = "Пятёрочка",
        shopAddress = "Москва, Каширское ш., 31",
        category = "Продукты",
        dateText = "25 окт. 2026, 18:40",
        items = listOf(
            ReceiptItem("1_1", "Молоко домик в деревне 3.2%", 2, 95),
            ReceiptItem("1_2", "Филе куриное охлажденное", 1, 420),
            ReceiptItem("1_3", "Сыр Российский 200г", 1, 240),
            ReceiptItem("1_4", "Кофе зерновой 1кг", 1, 1370)
        )
    ),
    Receipt(
        id = "2",
        shopName = "Яндекс Заправки",
        shopAddress = "Москва, Варшавское ш., 141",
        category = "Транспорт",
        dateText = "24 окт. 2026, 09:15",
        items = listOf(
            ReceiptItem("2_1", "АИ-95 (35 л)", 35, 60)
        )
    ),
    Receipt(
        id = "3",
        shopName = "Вкусно и точка",
        shopAddress = "Москва, м. Каширская",
        category = "Кафе",
        dateText = "24 окт. 2026, 13:20",
        items = listOf(
            ReceiptItem("3_1", "Биг Спешиал Комбо", 1, 465),
            ReceiptItem("3_2", "Наггетсы 9 шт.", 1, 165),
            ReceiptItem("3_3", "Капучино большой", 1, 170)
        )
    ),
    Receipt(
        id = "4",
        shopName = "Киномакс",
        shopAddress = "ТРЦ Москворечье",
        category = "Развлечения",
        dateText = "22 окт. 2026, 20:00",
        items = listOf(
            ReceiptItem("4_1", "Билет в кино VIP", 2, 650),
            ReceiptItem("4_2", "Попкорн карамельный большой", 1, 500)
        )
    )
)