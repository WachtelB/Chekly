package com.nordix.chekly.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nordix.chekly.challenge.Challenge
import com.nordix.chekly.challenge.ChallengeListScreen

@Composable
fun MainScreenContainer(
    challenges: List<Challenge>,
    onShareChallenge: (Challenge) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onAction(MainAction.MessageShown)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            CheklyBottomBar(
                currentTab = uiState.currentTab,
                onTabSelected = { tab -> viewModel.onAction(MainAction.TabSelected(tab)) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.currentTab == 4) {
                ChallengeListScreen(
                    challenges = challenges,
                    onShare = onShareChallenge
                )
            } else {
                MainScreen(
                    state = uiState,
                    onAction = viewModel::onAction
                )
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