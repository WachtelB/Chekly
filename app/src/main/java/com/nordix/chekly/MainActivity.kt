package com.nordix.chekly

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.nordix.chekly.challenge.shareChallenge
import com.nordix.chekly.main.screen.MainScreenContainer
import com.nordix.chekly.navigation.ReceiptDeepLink
import com.nordix.chekly.ui.theme.CheklyTheme

class MainActivity : LoggedActivity() {

    private var deepLinkReceiptId by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (savedInstanceState == null) {
            deepLinkReceiptId = ReceiptDeepLink.parseReceiptId(intent?.data)
        }

        setContent {
            CheklyTheme {
                MainScreenContainer(
                    initialDeepLinkReceiptId = deepLinkReceiptId,
                    onDeepLinkConsumed = { deepLinkReceiptId = null },
                    onShareChallenge = { challenge ->
                        shareChallenge(this@MainActivity, challenge)
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        ReceiptDeepLink.parseReceiptId(intent.data)?.let { id ->
            deepLinkReceiptId = id
        }
    }
}