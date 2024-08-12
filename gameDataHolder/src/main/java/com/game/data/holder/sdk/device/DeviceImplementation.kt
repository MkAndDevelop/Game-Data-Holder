package com.game.data.holder.sdk.device

import com.facebook.FacebookSdk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.UUID

internal class DeviceImplementation : DeviceRepository {

    private suspend fun getGoogleAdId(): String? = withContext(Dispatchers.IO) {
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

    override suspend fun googleAdId(): String? {
        var attempts = 0
        while (!FacebookSdk.isInitialized() && attempts < 8) {
            delay(500)
            attempts++
        }
        return if (FacebookSdk.isInitialized()) getGoogleAdId()
        else null
    }

    override fun getUUID(): String = UUID.randomUUID().toString()
}