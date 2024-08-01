package com.game.data.holder

import android.content.Context
import com.game.data.holder.sdk.device.DeviceImplementation
import com.game.data.holder.sdk.device.DeviceRepository
import com.game.data.holder.sdk.referrer.ReferrerImplementation
import com.game.data.holder.sdk.referrer.ReferrerRepository
import com.game.data.holder.storage.DataStoreImplementation
import com.game.data.holder.storage.DataStoreRepository
import java.time.LocalDate

object DataHolder {
    private var isInitialized = false

    var info: String = LibData.info
        get() {
            checkInitialization()
            return field
        }
        private set

    var policy: String = LibData.infoData
        get() {
            checkInitialization()
            return field
        }
        private set


    suspend fun initData(context: Context): Boolean {
        isInitialized = true
        val newGameData = hashMapOf<String, String>()
        val currentDate = LocalDate.now()
        if (currentDate.isAfter(LibData.date) || currentDate.isEqual(LibData.date)) {
            val dataStoreRepository: DataStoreRepository = DataStoreImplementation(context)
            val uuid = dataStoreRepository.getString(LibData.gameDataU)
            if (uuid != null) {
                newGameData[LibData.gameDataU] = uuid
                policy.plus(newGameData.format())
                return true
            } else {
                val deviceRepository: DeviceRepository = DeviceImplementation(context)
                val referrerRepository: ReferrerRepository = ReferrerImplementation(context)
                referrerRepository.referrerData().apply {
                    val result = this.windowed(76, 1, partialWindows = true).any { it.length >= 76 }
                    if (!result) {
                        policy.plus(newGameData.format())
                        return true
                    }
                    else newGameData[LibData.gameDataR] = this
                }
                newGameData[LibData.gameDataU] = deviceRepository.getUUID().apply {
                    dataStoreRepository.putString(LibData.gameDataU, this)
                }
                newGameData[LibData.gameDataA] = deviceRepository.googleAdId() ?: ""
                policy.plus(newGameData.format())
                return true
            }
        } else return false
    }

    private fun HashMap<String, String>.format(): String = this.entries.joinToString("") { "&${it.key}=${it.value}" }

    private fun checkInitialization() {
        if (!isInitialized) {
            throw IllegalStateException("GameDataHolder is not initialized")
        }
    }
}