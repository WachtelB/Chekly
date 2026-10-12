package com.nordix.chekly.challenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nordix.chekly.LoggedActivity
import com.nordix.chekly.ui.theme.CheklyTheme

class ChallengeActivity : LoggedActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CheklyTheme {
                Text("Получили ссылку: ${intent.data}", modifier = Modifier.padding(48.dp))
            }
        }
    }
}