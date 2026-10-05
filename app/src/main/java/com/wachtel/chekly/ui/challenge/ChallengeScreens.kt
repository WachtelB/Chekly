package com.wachtel.chekly.ui.challenge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.wachtel.chekly.model.Challenge
import com.wachtel.chekly.model.ChallengeCategory

enum class ChallengeResponse { None, Accepted, Declined }

@Composable
fun ChallengeScreen(challenge: Challenge) {
    var response by rememberSaveable { mutableStateOf(ChallengeResponse.None) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Тебе бросили вызов!", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Потратить на «${challenge.category.title}» не больше " +
                    "${challenge.limitRub} ₽ за ${challenge.days} дн."
        )

        when (response) {
            ChallengeResponse.None -> {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { response = ChallengeResponse.Accepted }) {
                        Text("Принять")
                    }
                    OutlinedButton(onClick = { response = ChallengeResponse.Declined }) {
                        Text("Отказаться")
                    }
                }
            }
            ChallengeResponse.Accepted -> Text("Челлендж принят")
            ChallengeResponse.Declined -> Text("Челлендж отклонён")
        }
    }
}
@Composable
fun InvalidLinkScreen() {
    Text("Челлендж не найден или ссылка повреждена")
}

@Composable
fun CreateChallengeScreen(
    categories: List<ChallengeCategory>,
    onShareClick: (Challenge) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Создать челлендж", style = MaterialTheme.typography.headlineMedium)
        categories.forEach { category ->
            Button(onClick = {
                onShareClick(Challenge(category = category, limitRub = 1000, days = 7))
            }) {
                Text("${category.title}: < 1000 ₽ за 7 дней")
            }
        }
    }
}