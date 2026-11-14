package com.nordix.chekly.challenge.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nordix.chekly.R
import com.nordix.chekly.challenge.Challenge
import com.nordix.chekly.ui.theme.CheklyDimens

@Composable
fun ChallengeListScreen(
    challenges: List<Challenge>,
    onShare: (Challenge) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(CheklyDimens.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(CheklyDimens.SpacingSmall),
    ) {
        items(items = challenges, key = { it.category }) { challenge ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = stringResource(
                        R.string.challenge_description,
                        challenge.category,
                        challenge.limitRub,
                        challenge.days,
                    ),
                    modifier = Modifier.weight(1f),
                )
                Button(onClick = { onShare(challenge) }) {
                    Text(stringResource(R.string.button_share))
                }
            }
        }
    }
}