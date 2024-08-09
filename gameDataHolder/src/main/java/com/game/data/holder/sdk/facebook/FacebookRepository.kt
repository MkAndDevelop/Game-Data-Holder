package com.game.data.holder.sdk.facebook

interface FacebookRepository {
    suspend fun referrer(): String?
}