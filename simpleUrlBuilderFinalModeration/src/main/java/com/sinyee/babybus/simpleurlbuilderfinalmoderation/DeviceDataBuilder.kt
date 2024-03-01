package com.sinyee.babybus.simpleurlbuilderfinalmoderation

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.appsflyer.AppsFlyerLib
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.sinyee.babybus.simpleurlbuilderfinalmoderation.utils.AppConst
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class DeviceDataBuilder(
    private val context: Context,
    private val battery: String,
    private val isDevSettings: Boolean?,
    private val devKey: String,
    private val facebookId: String,
    private val facebookToken: String
) {

    @SuppressLint("HardwareIds")
    private fun getAfUserId(): String? = try {
        AppsFlyerLib.getInstance().getAppsFlyerUID(context)
    } catch (e: Exception) {
        null
    }

    private suspend fun googleAdId(): String? = withContext(Dispatchers.IO) {
        try {
            AdvertisingIdClient.getAdvertisingIdInfo(context).id
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getDeviceInfoMap(): LinkedHashMap<String, String> {
        val hashMap: LinkedHashMap<String, String> = LinkedHashMap()
        hashMap[AppConst.GOOGLE_AD_ID] = googleAdId().toString()
        hashMap[AppConst.AF_USER_ID] = getAfUserId().toString()
        hashMap[AppConst.DEV_SETTINGS] = isDevSettings.toString()
        hashMap[AppConst.BATTERY] = battery
        hashMap[AppConst.BUNDLE] = context.packageName
        hashMap[AppConst.DEV] = devKey
        hashMap[AppConst.FB_APP_ID] = facebookId
        hashMap[AppConst.FB_AT] = facebookToken
        return hashMap
    }

    suspend fun getDeviceInfoUseCase(): UserData {
        val deviceInfoData = getDeviceInfoMap()
        val str = StringBuilder()
        deviceInfoData.forEach { key ->
            str.append("&${key.key}=${key.value}")
        }
        return UserData(str.toString(), deviceInfoData[AppConst.AF_USER_ID].toString())
    }
}