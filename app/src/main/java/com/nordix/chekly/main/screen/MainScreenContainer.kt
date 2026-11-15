package com.nordix.chekly.main.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowSizeClass
import com.nordix.chekly.challenge.Challenge
import com.nordix.chekly.challenge.screen.ChallengeListScreen
import com.nordix.chekly.history.HistoryListScreenContainer
import com.nordix.chekly.history.ReceiptDetailScreenContainer
import com.nordix.chekly.history.ReceiptEditScreenContainer
import com.nordix.chekly.main.AppTab
import com.nordix.chekly.main.MainAction
import com.nordix.chekly.main.MainViewModel
import com.nordix.chekly.main.components.MonthPickerBottomSheet
import com.nordix.chekly.main.states.ErrorStateView
import com.nordix.chekly.navigation.HistoryListKey
import com.nordix.chekly.navigation.HomeDashboardKey
import com.nordix.chekly.navigation.HomeReceiptPreviewKey
import com.nordix.chekly.navigation.ReceiptDetailKey
import com.nordix.chekly.navigation.ReceiptEditKey
import com.nordix.chekly.navigation.selectHistoryReceipt
import com.nordix.chekly.navigation.selectHomeReceiptPreview

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainScreenContainer(
    onShareChallenge: (Challenge) -> Unit,
    modifier: Modifier = Modifier,
    initialDeepLinkReceiptId: String? = null,
    onDeepLinkConsumed: () -> Unit = {},
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    // Вызываем без <NavKey>, как на слайдах 16 и 57
    val homeBackStack = rememberNavBackStack(HomeDashboardKey)
    val historyBackStack = rememberNavBackStack(HistoryListKey)

    // Используем V2 и isWidthAtLeastBreakpoint со слайда 51
    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    val isExpandedWindow = adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(
        WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
    )
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(
        directive = calculatePaneScaffoldDirective(adaptiveInfo),
        backNavigationBehavior = BackNavigationBehavior.PopLatest
    )

    LaunchedEffect(initialDeepLinkReceiptId) {
        initialDeepLinkReceiptId?.let { receiptId ->
            viewModel.onAction(MainAction.TabSelected(AppTab.HISTORY))
            historyBackStack.selectHistoryReceipt(receiptId)
            onDeepLinkConsumed()
        }
    }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { message ->
            snackBarHostState.showSnackbar(message)
            viewModel.onAction(MainAction.MessageShown)
        }
    }

    NavigationSuiteScaffold(
        modifier = modifier.fillMaxSize(),
        navigationSuiteItems = {
            item(
                selected = uiState.currentTab == AppTab.HOME,
                onClick = { viewModel.onAction(MainAction.TabSelected(AppTab.HOME)) },
                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                label = { Text("Главная") }
            )
            item(
                selected = uiState.currentTab == AppTab.HISTORY,
                onClick = { viewModel.onAction(MainAction.TabSelected(AppTab.HISTORY)) },
                icon = { Icon(Icons.Default.Menu, contentDescription = null) },
                label = { Text("История") }
            )
            item(
                selected = uiState.currentTab == AppTab.ADD,
                onClick = { viewModel.onAction(MainAction.TabSelected(AppTab.ADD)) },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                label = { Text("Добавить") }
            )
            item(
                selected = uiState.currentTab == AppTab.BUDGET,
                onClick = { viewModel.onAction(MainAction.TabSelected(AppTab.BUDGET)) },
                icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                label = { Text("Бюджет") }
            )
            item(
                selected = uiState.currentTab == AppTab.CHALLENGES,
                onClick = { viewModel.onAction(MainAction.TabSelected(AppTab.CHALLENGES)) },
                icon = { Icon(Icons.Default.Star, contentDescription = null) },
                label = { Text("Челленджи") }
            )
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (uiState.currentTab) {
                    AppTab.HOME -> {
                        NavDisplay(
                            backStack = homeBackStack,
                            onBack = { homeBackStack.removeLastOrNull() },
                            sceneStrategies = listOf(listDetailStrategy),
                            entryDecorators = listOf(
                                rememberSaveableStateHolderNavEntryDecorator(),
                                rememberViewModelStoreNavEntryDecorator()
                            ),
                            entryProvider = entryProvider<NavKey> {
                                entry<HomeDashboardKey>(
                                    metadata = ListDetailSceneStrategy.listPane(
                                        sceneKey = "home_scene",
                                        detailPlaceholder = {
                                            EmptyDetailPlaceholder("Выберите чек слева, чтобы просмотреть его состав")
                                        }
                                    )
                                ) {
                                    MainScreen(
                                        state = uiState,
                                        onAction = { action ->
                                            when (action) {
                                                is MainAction.ReceiptClicked -> {
                                                    if (isExpandedWindow) {
                                                        homeBackStack.selectHomeReceiptPreview(action.receiptId)
                                                    } else {
                                                        viewModel.onAction(MainAction.TabSelected(AppTab.HISTORY))
                                                        historyBackStack.selectHistoryReceipt(action.receiptId)
                                                    }
                                                }
                                                else -> viewModel.onAction(action)
                                            }
                                        }
                                    )
                                }

                                entry<HomeReceiptPreviewKey>(
                                    metadata = ListDetailSceneStrategy.detailPane(sceneKey = "home_scene")
                                ) { key ->
                                    ReceiptDetailScreenContainer(
                                        receiptId = key.receiptId,
                                        allowEdit = isExpandedWindow,
                                        onEditClick = { id ->
                                            viewModel.onAction(MainAction.TabSelected(AppTab.HISTORY))
                                            historyBackStack.selectHistoryReceipt(id)
                                            historyBackStack.add(ReceiptEditKey(id))
                                        },
                                        onBackClick = { homeBackStack.removeLastOrNull() }
                                    )
                                }
                            }
                        )
                    }

                    AppTab.HISTORY -> {
                        NavDisplay(
                            backStack = historyBackStack,
                            onBack = { historyBackStack.removeLastOrNull() },
                            sceneStrategies = listOf(listDetailStrategy),
                            entryDecorators = listOf(
                                rememberSaveableStateHolderNavEntryDecorator(),
                                rememberViewModelStoreNavEntryDecorator()
                            ),
                            entryProvider = entryProvider<NavKey> {
                                entry<HistoryListKey>(
                                    metadata = ListDetailSceneStrategy.listPane(
                                        sceneKey = "history_scene",
                                        detailPlaceholder = {
                                            EmptyDetailPlaceholder("Выберите чек в списке, чтобы увидеть позиции и цены")
                                        }
                                    )
                                ) {
                                    HistoryListScreenContainer(
                                        onReceiptClick = { id ->
                                            historyBackStack.selectHistoryReceipt(id)
                                        }
                                    )
                                }

                                entry<ReceiptDetailKey>(
                                    metadata = ListDetailSceneStrategy.detailPane(sceneKey = "history_scene")
                                ) { key ->
                                    ReceiptDetailScreenContainer(
                                        receiptId = key.receiptId,
                                        allowEdit = true,
                                        onEditClick = { id ->
                                            historyBackStack.add(ReceiptEditKey(id))
                                        },
                                        onBackClick = { historyBackStack.removeLastOrNull() }
                                    )
                                }

                                entry<ReceiptEditKey> { key ->
                                    ReceiptEditScreenContainer(
                                        receiptId = key.receiptId,
                                        onBack = { historyBackStack.removeLastOrNull() }
                                    )
                                }
                            }
                        )
                    }

                    AppTab.ADD, AppTab.BUDGET -> {
                        ErrorStateView(
                            onRetryClick = { viewModel.onAction(MainAction.Retry) }
                        )
                    }

                    AppTab.CHALLENGES -> {
                        ChallengeListScreen(
                            challenges = uiState.challenges,
                            onShare = onShareChallenge
                        )
                    }
                }

                if (uiState.isMonthPickerVisible) {
                    MonthPickerBottomSheet(
                        currentMonth = uiState.selectedMonth,
                        currentYear = uiState.selectedYear,
                        onDismiss = { viewModel.onAction(MainAction.DismissMonthPicker) },
                        onApply = { month, year ->
                            viewModel.onAction(MainAction.SelectPeriod(month, year))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyDetailPlaceholder(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}