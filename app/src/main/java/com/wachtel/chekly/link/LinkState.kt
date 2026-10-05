package com.wachtel.chekly.link

import com.wachtel.chekly.model.Challenge

sealed interface LinkState {
    data object None : LinkState
    data class Ok(val challenge: Challenge) : LinkState
    data object Invalid : LinkState
}