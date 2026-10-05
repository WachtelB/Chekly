package com.wachtel.chekly

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.wachtel.chekly.data.ChallengeCategoryRepository
import com.wachtel.chekly.data.InMemoryChallengeCategoryRepository
import com.wachtel.chekly.link.ChallengeLinkBuilder
import com.wachtel.chekly.link.ChallengeLinkParser
import com.wachtel.chekly.link.DefaultChallengeLinkBuilder
import com.wachtel.chekly.link.DefaultChallengeLinkParser
import com.wachtel.chekly.link.LinkState
import com.wachtel.chekly.ui.challenge.ChallengeScreen
import com.wachtel.chekly.ui.challenge.CreateChallengeScreen
import com.wachtel.chekly.ui.challenge.InvalidLinkScreen

class MainActivity : ComponentActivity() {

    // "Ручная" сборка зависимостей — вместо Hilt/Dagger, пока проект маленький.
    private val categoryRepository: ChallengeCategoryRepository =
        InMemoryChallengeCategoryRepository()
    private val linkParser: ChallengeLinkParser =
        DefaultChallengeLinkParser(categoryRepository)
    private val linkBuilder: ChallengeLinkBuilder =
        DefaultChallengeLinkBuilder()

    private var linkState by mutableStateOf<LinkState>(LinkState.None)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        linkState = linkParser.parse(intent.data)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val context = LocalContext.current
                    when (val state = linkState) {
                        is LinkState.Ok -> ChallengeScreen(state.challenge)
                        LinkState.Invalid -> InvalidLinkScreen()
                        LinkState.None -> CreateChallengeScreen(
                            categories = categoryRepository.all(),
                            onShareClick = { challenge ->
                                shareChallenge(context, challenge)
                            },
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent")
        setIntent(intent)
        linkState = linkParser.parse(intent.data)
    }

    private fun shareChallenge(context: android.content.Context, challenge: com.wachtel.chekly.model.Challenge) {
        val link = linkBuilder.build(challenge)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                link
            )
        }
        context.startActivity(Intent.createChooser(sendIntent, "Поделиться челленджем"))
    }

    override fun onStart() { super.onStart(); Log.d(TAG, "onStart") }
    override fun onResume() { super.onResume(); Log.d(TAG, "onResume") }
    override fun onPause() { super.onPause(); Log.d(TAG, "onPause") }
    override fun onStop() { super.onStop(); Log.d(TAG, "onStop") }
    override fun onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy") }

    private companion object {
        const val TAG = "Lifecycle"
    }
}