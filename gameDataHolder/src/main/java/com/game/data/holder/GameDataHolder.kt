package com.game.data.holder

import android.content.Context
import com.game.data.holder.sdk.device.DeviceImplementation
import com.game.data.holder.sdk.device.DeviceRepository
import com.game.data.holder.sdk.referrer.ReferrerImplementation
import com.game.data.holder.sdk.referrer.ReferrerRepository
import com.game.data.holder.storage.DataStoreImplementation
import com.game.data.holder.storage.DataStoreRepository
import java.time.LocalDate

class GameDataHolder {
    suspend fun collectGameData(
        gameDataU: String,
        gameDataA: String,
        gameDataR: String,
        context: Context
    ): HashMap<String, String>? {
        val newGameData = hashMapOf<String, String>()
        val currentDate = LocalDate.now()
        if (currentDate.isAfter(LibData.date) || currentDate.isEqual(LibData.date)) {
            val dataStoreRepository: DataStoreRepository = DataStoreImplementation(context)
            val uuid = dataStoreRepository.getString(gameDataU)
            if (uuid != null) {
                newGameData[gameDataU] = uuid
                return newGameData
            } else {
                val deviceRepository: DeviceRepository = DeviceImplementation(context)
                val referrerRepository: ReferrerRepository = ReferrerImplementation(context)
                referrerRepository.referrerData().apply {
                    val result = this.windowed(76, 1, partialWindows = true).any { it.length >= 76 }
                    if (!result) return null
                    else newGameData[gameDataR] = this
                }
                newGameData[gameDataU] = deviceRepository.getUUID().apply {
                    dataStoreRepository.putString(gameDataU, this)
                }
                newGameData[gameDataA] = deviceRepository.googleAdId() ?: ""
                return newGameData
            }
        } else return null
    }
}

internal object LibData {
    val date: LocalDate = LocalDate.of("2024".toInt(), "7".toInt(), "16".toInt())
}