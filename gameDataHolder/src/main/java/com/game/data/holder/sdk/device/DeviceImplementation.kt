package com.game.data.holder.sdk.device

import android.content.Context
import com.game.data.holder.sdk.device.DeviceRepository
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

internal class DeviceImplementation(private val context: Context) : DeviceRepository {

    override suspend fun googleAdId(): String? = withContext(Dispatchers.IO) {
        try {
            AdvertisingIdClient.getAdvertisingIdInfo(context).id
        } catch (_: Exception) {
            null
        }
    }

    override fun getUUID(): String = UUID.randomUUID().toString()
}