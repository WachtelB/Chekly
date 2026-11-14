package com.nordix.chekly.challenge.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nordix.chekly.R
import com.nordix.chekly.challenge.ChallengeAction
import com.nordix.chekly.challenge.ChallengeResult
import com.nordix.chekly.challenge.ChallengeUiState
import com.nordix.chekly.ui.theme.CheklyDimens

@Composable
fun ChallengeScreen(
    state: ChallengeUiState,
    onAction: (ChallengeAction) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(CheklyDimens.SheetHorizontalPadding),
        verticalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingLarge)
    ) {
        when (val result = state.result) {
            ChallengeResult.Loading -> {
                CircularProgressIndicator()
                Text(text = stringResource(R.string.state_loading))
            }
            ChallengeResult.Empty -> {
                Text(
                    text = stringResource(R.string.challenge_invalid),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            is ChallengeResult.Error -> {
                Text(
                    text = result.message,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error
                )
                Button(onClick = { onAction(ChallengeAction.Retry) }) {
                    Text(stringResource(R.string.button_retry))
                }
            }
            is ChallengeResult.Content -> {
                Text(
                    text = stringResource(R.string.challenge_title),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = stringResource(
                        R.string.challenge_description,
                        result.challenge.category,
                        result.challenge.limitRub,
                        result.challenge.days
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )
                OutlinedTextField(
                    value = state.note,
                    onValueChange = { onAction(ChallengeAction.NoteChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.challenge_note_label)) },
                    singleLine = true
                )
                if (result.accepted) {
                    Text(
                        text = stringResource(R.string.challenge_accepted),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )
                } else {
                    Button(onClick = { onAction(ChallengeAction.AcceptClicked) }) {
                        Text(stringResource(R.string.challenge_accept))
                    }
                }
            }
        }
        TextButton(onClick = onClose) {
            Text(stringResource(R.string.challenge_close))
        }
    }
}