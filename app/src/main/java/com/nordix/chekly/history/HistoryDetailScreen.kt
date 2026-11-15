package com.nordix.chekly.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nordix.chekly.data.Receipt
import com.nordix.chekly.data.ReceiptRepository
import com.nordix.chekly.main.states.ErrorStateView
import com.nordix.chekly.ui.theme.CheklyDimens

@Composable
fun ReceiptDetailScreenContainer(
    receiptId: String,
    allowEdit: Boolean,
    onEditClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val receipts by ReceiptRepository.receipts.collectAsStateWithLifecycle()
    val receipt = remember(receipts, receiptId) {
        receipts.find { it.id == receiptId }
    }

    ReceiptDetailScreen(
        receiptId = receiptId,
        receipt = receipt,
        allowEdit = allowEdit,
        onEditClick = { onEditClick(receiptId) },
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@Composable
fun ReceiptDetailScreen(
    receiptId: String,
    receipt: Receipt?,
    allowEdit: Boolean,
    onEditClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (receipt == null) {
        ErrorStateView(
            message = "Чек с ID «$receiptId» не найден или был удалён.",
            buttonText = "Назад к списку",
            onRetryClick = onBackClick,
            modifier = modifier
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(CheklyDimens.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingMedium)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingSmall)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад"
                )
            }
            Text(
                text = "Детали чека",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            if (allowEdit) {
                Button(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Text(
                        text = "Изменить",
                        modifier = Modifier.padding(start = CheklyDimens.SpacingSmall)
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(CheklyDimens.CardPadding),
                verticalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingSmall)
            ) {
                Text(text = receipt.shopName, style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = "${receipt.category} • ${receipt.dateText}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Адрес: ${receipt.shopAddress}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = CheklyDimens.SpacingSmall))

                Text(
                    text = "Позиции в чеке (${receipt.items.size}):",
                    style = MaterialTheme.typography.titleMedium
                )

                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingSmall)
                ) {
                    items(receipt.items, key = { it.id }) { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.name} × ${item.quantity}",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${item.totalRub} ₽",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = CheklyDimens.SpacingSmall))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "ИТОГО:", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = "${receipt.amountRub} ₽",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}