package com.wachtel.chekly.link

import android.net.Uri
import android.util.Log
import com.wachtel.chekly.data.ChallengeCategoryRepository
import com.wachtel.chekly.model.Challenge

interface ChallengeLinkParser {
    fun parse(uri: Uri?): LinkState
}

class DefaultChallengeLinkParser(
    private val categoryRepository: ChallengeCategoryRepository,
) : ChallengeLinkParser {

    override fun parse(uri: Uri?): LinkState {
        Log.d("LinkParser", "parse: uri=$uri")
        if (uri == null) return LinkState.None

        val categoryKey = uri.getQueryParameter("category")
        Log.d("LinkParser", "categoryKey=$categoryKey")
        if (categoryKey == null) return LinkState.Invalid

        val category = categoryRepository.findByKey(categoryKey)
        Log.d("LinkParser", "category=$category")
        if (category == null) return LinkState.Invalid

        val limit = uri.getQueryParameter("limit")?.toIntOrNull()
        Log.d("LinkParser", "limit=$limit")
        if (limit == null || limit <= 0) return LinkState.Invalid

        val days = uri.getQueryParameter("days")?.toIntOrNull()
        Log.d("LinkParser", "days=$days")
        if (days == null || days !in 1..365) return LinkState.Invalid

        return LinkState.Ok(Challenge(category, limit, days))
    }
}