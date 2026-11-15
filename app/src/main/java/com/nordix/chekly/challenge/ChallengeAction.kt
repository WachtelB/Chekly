package com.nordix.chekly.challenge

sealed interface ChallengeAction {
    data class NoteChanged(val note: String) : ChallengeAction
    data object AcceptClicked : ChallengeAction
    data object Retry : ChallengeAction
    data object MessageShown : ChallengeAction
}