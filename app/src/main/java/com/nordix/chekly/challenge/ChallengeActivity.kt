package com.nordix.chekly.challenge

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.nordix.chekly.LoggedActivity
import com.nordix.chekly.ui.theme.CheklyTheme

class ChallengeActivity : LoggedActivity() {

    private val viewModel: ChallengeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (savedInstanceState == null) {
            viewModel.handleIncomingUri(intent?.data)
        }

        setContent {
            CheklyTheme {
                ChallengeScreenContainer(
                    viewModel = viewModel,
                    onClose = { finish() }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        viewModel.handleIncomingUri(intent.data)
    }
}