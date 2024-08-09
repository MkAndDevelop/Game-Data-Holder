package com.game.data.holder.sdk.device

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

internal class DeviceImplementation : DeviceRepository {

    override suspend fun googleAdId(): String? = withContext(Dispatchers.IO) {
        try {
            val attributionClass = Class.forName("com.facebook.internal.AttributionIdentifiers")
            val cachedIdentifiersField = attributionClass.getDeclaredField("cachedIdentifiers")
            cachedIdentifiersField.isAccessible = true
            val cachedIdentifiers = cachedIdentifiersField.get(null)
            cachedIdentifiers?.let {
                val attributionIdField = attributionClass.getDeclaredField("androidAdvertiserIdValue")
                attributionIdField.isAccessible = true
                attributionIdField.get(it) as? String
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun getUUID(): String = UUID.randomUUID().toString()
}