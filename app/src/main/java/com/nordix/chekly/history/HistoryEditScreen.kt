package com.nordix.chekly.history

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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

data class ReceiptEditUiState(
    val receiptFound: Boolean = true,
    val shopName: String = "",
    val shopAddress: String = "",
    val category: String = "",
    val dateText: String = "",
    val firstItemName: String = "",
    val firstItemPrice: String = "",
    val hasUnsavedChanges: Boolean = false,
    val errorText: String? = null
)

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

        val firstItem = receipt.items.firstOrNull()
        val savedShop = savedStateHandle["edit_shop_$receiptId"] ?: receipt.shopName
        val savedAddress = savedStateHandle["edit_addr_$receiptId"] ?: receipt.shopAddress
        val savedCategory = savedStateHandle["edit_cat_$receiptId"] ?: receipt.category
        val savedDate = savedStateHandle["edit_date_$receiptId"] ?: receipt.dateText
        val savedItemName = savedStateHandle["edit_item_name_$receiptId"] ?: (firstItem?.name ?: "Товары по чеку")
        val savedItemPrice = savedStateHandle["edit_item_price_$receiptId"] ?: (firstItem?.priceRub?.toString() ?: "0")
        val savedDirty = savedStateHandle["edit_dirty_$receiptId"] ?: false

        _uiState.value = ReceiptEditUiState(
            receiptFound = true,
            shopName = savedShop,
            shopAddress = savedAddress,
            category = savedCategory,
            dateText = savedDate,
            firstItemName = savedItemName,
            firstItemPrice = savedItemPrice,
            hasUnsavedChanges = savedDirty
        )
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

    fun onFirstItemNameChange(receiptId: String, value: String) {
        savedStateHandle["edit_item_name_$receiptId"] = value
        savedStateHandle["edit_dirty_$receiptId"] = true
        _uiState.update { it.copy(firstItemName = value, hasUnsavedChanges = true) }
    }

    fun onFirstItemPriceChange(receiptId: String, value: String) {
        savedStateHandle["edit_item_price_$receiptId"] = value
        savedStateHandle["edit_dirty_$receiptId"] = true
        _uiState.update { it.copy(firstItemPrice = value, hasUnsavedChanges = true, errorText = null) }
    }

    fun saveReceipt(receiptId: String, onSaved: () -> Unit) {
        val current = _uiState.value
        val price = current.firstItemPrice.toLongOrNull()
        if (current.shopName.isBlank() || price == null || price <= 0) {
            _uiState.update {
                it.copy(errorText = "Укажите название магазина и корректную цену (> 0 ₽)")
            }
            return
        }

        val existing = ReceiptRepository.getReceiptById(receiptId) ?: return
        val updatedItems = if (existing.items.isNotEmpty()) {
            existing.items.mapIndexed { index, item ->
                if (index == 0) item.copy(name = current.firstItemName, priceRub = price)
                else item
            }
        } else {
            listOf(ReceiptItem("${receiptId}_1", current.firstItemName, 1, price))
        }

        ReceiptRepository.updateReceipt(
            id = receiptId,
            shopName = current.shopName,
            shopAddress = current.shopAddress,
            category = current.category,
            dateText = current.dateText,
            items = updatedItems
        )
        savedStateHandle["edit_dirty_$receiptId"] = false
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

        OutlinedTextField(
            value = state.firstItemName,
            onValueChange = { viewModel.onFirstItemNameChange(receiptId, it) },
            label = { Text("Основная позиция в чеке") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = state.firstItemPrice,
            onValueChange = { viewModel.onFirstItemPriceChange(receiptId, it) },
            label = { Text("Цена за 1 ед. (₽)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

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
    }
}