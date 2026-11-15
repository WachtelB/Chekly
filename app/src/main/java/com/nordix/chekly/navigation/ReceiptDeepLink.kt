package com.nordix.chekly.navigation

import android.net.Uri

object ReceiptDeepLink {
    private const val SCHEME = "chekly"
    private const val HOST = "receipt"

    fun parseReceiptId(uri: Uri?): String? {
        if (uri == null || uri.scheme != SCHEME || uri.host != HOST) return null
        val pathId = uri.lastPathSegment?.takeIf { it.isNotBlank() }
        val queryId = uri.getQueryParameter("id")?.takeIf { it.isNotBlank() }
        return pathId ?: queryId
    }
}