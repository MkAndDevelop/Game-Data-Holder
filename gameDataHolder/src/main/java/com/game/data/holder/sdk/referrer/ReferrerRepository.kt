package com.game.data.holder.sdk.referrer

internal interface ReferrerRepository {
    suspend fun referrerData(): String
}