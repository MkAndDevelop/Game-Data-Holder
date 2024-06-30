package com.game.data.holder.sdk.device

internal interface DeviceRepository {
    suspend fun googleAdId(): String?
    fun getUUID(): String
}