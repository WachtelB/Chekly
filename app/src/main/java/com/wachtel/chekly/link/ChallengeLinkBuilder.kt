package com.wachtel.chekly.link

import com.wachtel.chekly.model.Challenge

interface ChallengeLinkBuilder {
    fun build(challenge: Challenge): String
}

class DefaultChallengeLinkBuilder : ChallengeLinkBuilder {
    override fun build(challenge: Challenge): String =
        "chekly://challenge?category=${challenge.category.key}" +
                "&limit=${challenge.limitRub}" +
                "&days=${challenge.days}"
}