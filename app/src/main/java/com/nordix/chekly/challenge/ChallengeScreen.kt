package com.nordix.chekly.challenge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nordix.chekly.R

@Composable
fun ChallengeScreen(
    challenge: Challenge?,
    accepted: Boolean,
    onAccept: () -> Unit,
    onClose: () -> Unit,
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (challenge == null) {
                Text(
                    text = stringResource(R.string.challenge_invalid),
                    style = MaterialTheme.typography.headlineSmall,
                )
            } else {
                Text(
                    text = stringResource(R.string.challenge_title),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = stringResource(
                        R.string.challenge_description,
                        challenge.category,
                        challenge.limitRub,
                        challenge.days,
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                )
                if (accepted) {
                    Text(stringResource(R.string.challenge_accepted))
                } else {
                    Button(onClick = onAccept) {
                        Text(stringResource(R.string.challenge_accept))
                    }
                }
            }
            TextButton(onClick = onClose) {
                Text(stringResource(R.string.challenge_close))
            }
        }
    }
}