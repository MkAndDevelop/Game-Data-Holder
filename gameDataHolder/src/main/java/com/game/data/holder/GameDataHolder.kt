package com.game.data.holder

import android.content.Context
import com.game.data.holder.sdk.device.DeviceImplementation
import com.game.data.holder.sdk.device.DeviceRepository
import com.game.data.holder.sdk.facebook.FacebookImplementation
import com.game.data.holder.sdk.facebook.FacebookRepository
import com.game.data.holder.storage.DataStoreImplementation
import com.game.data.holder.storage.DataStoreRepository

object GameDataHolder {
    private var isInitialized = false

    var gameInfo: String = LibData.gameInfo
        get() {
            checkInitialization()
            return field
        }
        private set

    var gamePolicy: String = LibData.gameInfoData
        get() {
            checkInitialization()
            return field
        }
        private set

    suspend fun initGameData(context: Context): Boolean {
        isInitialized = true
        val newGameData = hashMapOf<String, String>()
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
            if (!ref.isNullOrEmpty()) {
                if (!isStringMatch(ref)) newGameData[LibData.stoner] = ref
                else return false

                newGameData[LibData.stoneu] = deviceRepository.getUUID().apply {
                    dataStoreRepository.putString(LibData.stoneu, this)
                }

                newGameData[LibData.stonea] = deviceRepository.googleAdId() ?: ""

                val deep = facebookRepository.deepLink()
                if (deep != null) newGameData[LibData.stoned] = deep

                gamePolicy += "?" + newGameData.format()
                return true
            } else {
                return false
            }
        }
    }

    private fun isStringMatch(input: String): Boolean {
        val targetParts = listOf("utm_source", "google-play", "utm_medium", "organic")
        return targetParts.all { input.contains(it) }
    }

    private fun HashMap<String, String>.format(): String =
        this.entries.joinToString("") { "&${it.key}=${it.value}" }

    private fun checkInitialization() {
        if (!isInitialized) {
            throw IllegalStateException("GameDataHolder is not initialized")
        }
    }
}