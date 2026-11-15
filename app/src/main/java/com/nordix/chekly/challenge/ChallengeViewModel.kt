package com.nordix.chekly.challenge

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private const val KEY_URI = "deeplink_uri"
private const val KEY_ACCEPTED = "deeplink_accepted"
private const val KEY_NOTE = "deeplink_note"

class ChallengeViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ChallengeUiState(
            note = savedStateHandle.get<String>(KEY_NOTE) ?: "",
            result = restoreResultFromSavedState()
        )
    )
    val uiState: StateFlow<ChallengeUiState> = _uiState.asStateFlow()

    fun handleIncomingUri(uri: Uri?) {
        val savedUri = savedStateHandle.get<String>(KEY_URI)
        val incomingUriString = uri?.toString()

        if (savedUri != null && savedUri == incomingUriString) {
            return
        }

        savedStateHandle[KEY_URI] = incomingUriString
        savedStateHandle[KEY_ACCEPTED] = false

        _uiState.update {
            it.copy(result = parseUriToResult(uri, false))
        }
    }

    fun onAction(action: ChallengeAction) {
        when (action) {
            is ChallengeAction.NoteChanged -> {
                savedStateHandle[KEY_NOTE] = action.note
                _uiState.update { it.copy(note = action.note) }
            }
            ChallengeAction.AcceptClicked -> {
                savedStateHandle[KEY_ACCEPTED] = true
                _uiState.update { current ->
                    val currentResult = current.result
                    if (currentResult is ChallengeResult.Content) {
                        current.copy(
                            result = currentResult.copy(accepted = true),
                            userMessage = "Челлендж «${currentResult.challenge.category}» принят!"
                        )
                    } else current
                }
            }
            ChallengeAction.Retry -> {
                val uri = savedStateHandle.get<String>(KEY_URI)?.let(Uri::parse)
                val accepted = savedStateHandle.get<Boolean>(KEY_ACCEPTED) ?: false
                _uiState.update {
                    it.copy(
                        result = parseUriToResult(uri, accepted),
                        userMessage = "Повторная проверка ссылки выполнена"
                    )
                }
            }
            ChallengeAction.MessageShown -> {
                _uiState.update { it.copy(userMessage = null) }
            }
        }
    }

    private fun restoreResultFromSavedState(): ChallengeResult {
        val uriString = savedStateHandle.get<String>(KEY_URI) ?: return ChallengeResult.Loading
        val accepted = savedStateHandle.get<Boolean>(KEY_ACCEPTED) ?: false
        return parseUriToResult(Uri.parse(uriString), accepted)
    }

    private fun parseUriToResult(uri: Uri?, accepted: Boolean): ChallengeResult {
        if (uri == null) return ChallengeResult.Empty
        return when (val parseResult = ChallengeLink.parse(uri)) {
            is ChallengeParseResult.Success -> {
                ChallengeResult.Content(challenge = parseResult.challenge, accepted = accepted)
            }
            is ChallengeParseResult.Failure -> {
                ChallengeResult.Error("Некорректная или повреждённая ссылка челленджа")
            }
        }
    }
}