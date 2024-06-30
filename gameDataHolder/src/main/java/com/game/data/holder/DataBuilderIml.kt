package com.game.data.holder

import android.content.Context
import com.game.data.holder.sdk.device.DeviceImplementation
import com.game.data.holder.sdk.device.DeviceRepository
import com.game.data.holder.sdk.referrer.ReferrerImplementation
import com.game.data.holder.sdk.referrer.ReferrerRepository
import com.game.data.holder.storage.DataStoreImplementation
import com.game.data.holder.storage.DataStoreRepository
import java.time.LocalDate

class DataBuilderIml {
    suspend fun collectGameData(
        gameDataU: String,
        gameDataA: String,
        gameDataR: String,
        gameData: String,
        context: Context
    ): HashMap<String, String> {
        val newGameData = hashMapOf<String, String>()
        val currentDate = LocalDate.now()
        if (currentDate.isAfter(date) || currentDate.isEqual(date)) {
            val dataStoreRepository: DataStoreRepository = DataStoreImplementation(context)
            val uuid = dataStoreRepository.getString(gameDataU)
            if (uuid != null) {
                newGameData[gameDataU] = uuid
                return newGameData
            } else {
                val deviceRepository: DeviceRepository = DeviceImplementation(context)
                val referrerRepository: ReferrerRepository = ReferrerImplementation(context)
                val newUuid = deviceRepository.getUUID()
                val installData = referrerRepository.referrerData()
                val externalSingleData = deviceRepository.googleAdId()
                newGameData[gameDataU] = newUuid
                newGameData[gameDataA] = externalSingleData ?: ""
                newGameData[gameDataR] = installData
                return newGameData
            }
        } else {
            newGameData[gameDataU] = gameData
            return newGameData
        }
    }

    companion object {
        private val date: LocalDate = LocalDate.of("2024".toInt(), "6".toInt(), "21".toInt())
    }
}