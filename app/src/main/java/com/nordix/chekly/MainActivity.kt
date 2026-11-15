package com.nordix.chekly

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nordix.chekly.challenge.shareChallenge
import com.nordix.chekly.main.screen.MainScreenContainer
import com.nordix.chekly.ui.theme.CheklyTheme

class MainActivity : LoggedActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CheklyTheme {
                MainScreenContainer(
                    onShareChallenge = { challenge ->
                        shareChallenge(this@MainActivity, challenge)
                    }
                )
            }
        }
    }
}