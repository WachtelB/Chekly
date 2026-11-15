package com.nordix.chekly.history

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nordix.chekly.data.ReceiptItem
import com.nordix.chekly.data.ReceiptRepository
import com.nordix.chekly.main.states.ErrorStateView
import com.nordix.chekly.ui.theme.CheklyDimens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Immutable
@Serializable
data class EditableReceiptItem(
    val id: String,
    val name: String,
    val quantityText: String,
    val priceText: String
) {
    val subtotalRub: Long
        get() = (quantityText.toIntOrNull()?.coerceAtLeast(0) ?: 0) *
                (priceText.toLongOrNull()?.coerceAtLeast(0L) ?: 0L)
}

@Immutable
data class ReceiptEditUiState(
    val receiptFound: Boolean = true,
    val shopName: String = "",
    val shopAddress: String = "",
    val category: String = "",
    val dateText: String = "",
    val items: List<EditableReceiptItem> = emptyList(),
    val hasUnsavedChanges: Boolean = false,
    val errorText: String? = null
) {
    val totalAmountRub: Long
        get() = items.sumOf { it.subtotalRub }
}

class ReceiptEditViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var initializedId: String? = null
    private val _uiState = MutableStateFlow(ReceiptEditUiState())
    val uiState: StateFlow<ReceiptEditUiState> = _uiState.asStateFlow()

    fun init(receiptId: String) {
        if (initializedId == receiptId) return
        initializedId = receiptId

        val receipt = ReceiptRepository.getReceiptById(receiptId)
        if (receipt == null) {
            _uiState.value = ReceiptEditUiState(receiptFound = false)
            return
        }

        val savedItemsJson = savedStateHandle.get<String>("edit_items_json_$receiptId")
        val initialItems = savedItemsJson?.let {
            runCatching { Json.decodeFromString<List<EditableReceiptItem>>(it) }.getOrNull()
        } ?: receipt.items.map {
            EditableReceiptItem(
                id = it.id,
                name = it.name,
                quantityText = it.quantity.toString(),
                priceText = it.priceRub.toString()
            )
        }

        val savedShop = savedStateHandle["edit_shop_$receiptId"] ?: receipt.shopName
        val savedAddress = savedStateHandle["edit_addr_$receiptId"] ?: receipt.shopAddress
        val savedCategory = savedStateHandle["edit_cat_$receiptId"] ?: receipt.category
        val savedDate = savedStateHandle["edit_date_$receiptId"] ?: receipt.dateText
        val savedDirty = savedStateHandle["edit_dirty_$receiptId"] ?: false

        _uiState.value = ReceiptEditUiState(
            receiptFound = true,
            shopName = savedShop,
            shopAddress = savedAddress,
            category = savedCategory,
            dateText = savedDate,
            items = initialItems,
            hasUnsavedChanges = savedDirty
        )
    }

    private fun saveItemsToHandle(receiptId: String, items: List<EditableReceiptItem>) {
        savedStateHandle["edit_items_json_$receiptId"] = Json.encodeToString(items)
        savedStateHandle["edit_dirty_$receiptId"] = true
    }

    fun onShopNameChange(receiptId: String, value: String) {
        savedStateHandle["edit_shop_$receiptId"] = value
        savedStateHandle["edit_dirty_$receiptId"] = true
        _uiState.update { it.copy(shopName = value, hasUnsavedChanges = true, errorText = null) }
    }

    fun onAddressChange(receiptId: String, value: String) {
        savedStateHandle["edit_addr_$receiptId"] = value
        savedStateHandle["edit_dirty_$receiptId"] = true
        _uiState.update { it.copy(shopAddress = value, hasUnsavedChanges = true) }
    }

    fun onCategoryChange(receiptId: String, value: String) {
        savedStateHandle["edit_cat_$receiptId"] = value
        savedStateHandle["edit_dirty_$receiptId"] = true
        _uiState.update { it.copy(category = value, hasUnsavedChanges = true) }
    }

    fun onItemChange(
        receiptId: String,
        itemId: String,
        name: String,
        quantity: String,
        price: String
    ) {
        _uiState.update { current ->
            val updatedItems = current.items.map { item ->
                if (item.id == itemId) {
                    item.copy(name = name, quantityText = quantity, priceText = price)
                } else item
            }
            saveItemsToHandle(receiptId, updatedItems)
            current.copy(items = updatedItems, hasUnsavedChanges = true, errorText = null)
        }
    }

    fun onAddItem(receiptId: String) {
        _uiState.update { current ->
            val newItem = EditableReceiptItem(
                id = "${receiptId}_${System.currentTimeMillis()}",
                name = "",
                quantityText = "1",
                priceText = ""
            )
            val updatedItems = current.items + newItem
            saveItemsToHandle(receiptId, updatedItems)
            current.copy(items = updatedItems, hasUnsavedChanges = true)
        }
    }

    fun onRemoveItem(receiptId: String, itemId: String) {
        _uiState.update { current ->
            if (current.items.size <= 1) return@update current
            val updatedItems = current.items.filterNot { it.id == itemId }
            saveItemsToHandle(receiptId, updatedItems)
            current.copy(items = updatedItems, hasUnsavedChanges = true)
        }
    }

    fun saveReceipt(receiptId: String, onSaved: () -> Unit) {
        val current = _uiState.value
        if (current.shopName.isBlank()) {
            _uiState.update { it.copy(errorText = "Укажите название магазина") }
            return
        }

        val validItems = current.items.mapNotNull { item ->
            val qty = item.quantityText.toIntOrNull()
            val price = item.priceText.toLongOrNull()
            if (item.name.isNotBlank() && qty != null && qty > 0 && price != null && price > 0) {
                ReceiptItem(
                    id = item.id,
                    name = item.name.trim(),
                    quantity = qty,
                    priceRub = price
                )
            } else null
        }

        if (validItems.size != current.items.size || validItems.isEmpty()) {
            _uiState.update {
                it.copy(errorText = "Проверьте все позиции: укажите название, количество > 0 и цену > 0 ₽")
            }
            return
        }

        ReceiptRepository.updateReceipt(
            id = receiptId,
            shopName = current.shopName,
            shopAddress = current.shopAddress,
            category = current.category,
            dateText = current.dateText,
            items = validItems
        )
        savedStateHandle["edit_dirty_$receiptId"] = false
        savedStateHandle["edit_items_json_$receiptId"] = null
        onSaved()
    }
}

@Composable
fun ReceiptEditScreenContainer(
    receiptId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: ReceiptEditViewModel = viewModel(key = "edit_$receiptId")
    viewModel.init(receiptId)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = state.hasUnsavedChanges) {
        showExitDialog = true
    }

    if (!state.receiptFound) {
        ErrorStateView(
            message = "Редактируемый чек не найден",
            onRetryClick = onBack,
            modifier = modifier
        )
        return
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Выйти без сохранения?") },
            text = { Text("Введённые изменения чека будут потеряны.") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    onBack()
                }) { Text("Выйти") }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) { Text("Остаться") }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(CheklyDimens.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingMedium)
    ) {
        Text(
            text = "Редактирование чека",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = state.shopName,
            onValueChange = { viewModel.onShopNameChange(receiptId, it) },
            label = { Text("Магазин") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = state.shopAddress,
            onValueChange = { viewModel.onAddressChange(receiptId, it) },
            label = { Text("Адрес магазина") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = state.category,
            onValueChange = { viewModel.onCategoryChange(receiptId, it) },
            label = { Text("Категория (Продукты, Кафе, Транспорт, Развлечения)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        HorizontalDivider()

        Text(
            text = "Позиции в чеке (${state.items.size}):",
            style = MaterialTheme.typography.titleMedium
        )

        state.items.forEachIndexed { index, item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(CheklyDimens.SpacingMedium),
                    verticalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingSmall)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Позиция #${index + 1}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (state.items.size > 1) {
                            IconButton(onClick = { viewModel.onRemoveItem(receiptId, item.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Удалить позицию",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = item.name,
                        onValueChange = {
                            viewModel.onItemChange(receiptId, item.id, it, item.quantityText, item.priceText)
                        },
                        label = { Text("Название товара") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingSmall)
                    ) {
                        OutlinedTextField(
                            value = item.quantityText,
                            onValueChange = {
                                viewModel.onItemChange(receiptId, item.id, item.name, it, item.priceText)
                            },
                            label = { Text("Кол-во (шт.)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = item.priceText,
                            onValueChange = {
                                viewModel.onItemChange(receiptId, item.id, item.name, item.quantityText, it)
                            },
                            label = { Text("Цена за 1 шт. (₽)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1.3f),
                            singleLine = true
                        )
                    }

                    Text(
                        text = "Сумма: ${item.subtotalRub} ₽",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        OutlinedButton(
            onClick = { viewModel.onAddItem(receiptId) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Text(
                text = "Добавить позицию",
                modifier = Modifier.padding(start = CheklyDimens.SpacingSmall)
            )
        }

        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "ИТОГО:", style = MaterialTheme.typography.titleLarge)
            Text(
                text = "${state.totalAmountRub} ₽",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        state.errorText?.let { error ->
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingMedium)
        ) {
            OutlinedButton(
                onClick = {
                    if (state.hasUnsavedChanges) showExitDialog = true else onBack()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Отмена")
            }
            Button(
                onClick = { viewModel.saveReceipt(receiptId, onSaved = onBack) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Сохранить")
            }
        }

        Spacer(modifier = Modifier.height(CheklyDimens.SpacingLarge))
    }
}