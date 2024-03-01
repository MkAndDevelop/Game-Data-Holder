package com.sinyee.babybus.simpleurlbuilderfinalmoderation.sdk

import android.app.Activity
import com.appsflyer.AFLogger
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.sinyee.babybus.simpleurlbuilderfinalmoderation.AppsData
import com.sinyee.babybus.simpleurlbuilderfinalmoderation.utils.AppConst
import com.sinyee.babybus.simpleurlbuilderfinalmoderation.utils.decrypt
import kotlinx.coroutines.isActive
import java.net.URLEncoder
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal object AppsFlayerDataBuilder {
    private val keys = arrayOf(
        AppConst.AF_STATUS,
        AppConst.CAMPAIGN,
        AppConst.MEDIA_SOURCE,
        AppConst.AF_CHANNEL,
        AppConst.AF_AD,
        AppConst.CAMPAIGN_ID,
        AppConst.ADSET_ID,
        AppConst.AD_ID,
        AppConst.ADSET
    )

    private suspend fun appsFlyerData(activity: Activity, devKey: String): Map<String, Any>? =
        suspendCoroutine { res ->
            val appsInstance = AppsFlyerLib.getInstance()
            appsInstance.setLogLevel(AFLogger.LogLevel.NONE)
            appsInstance.setCollectAndroidID(false)
            appsInstance.init(
                devKey,
                object : AppsFlyerConversionListener {
                    override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                        if (res.context.isActive) res.resume(data)
                    }

                    override fun onConversionDataFail(data: String?) {
                        res.resume(null)
                    }

                    override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                        res.resume(null)
                    }

                    override fun onAttributionFailure(data: String?) {
                        res.resume(null)
                    }

                }, activity
            ).start(activity)
        }

    suspend fun getAppsFlyerData(
        activity: Activity,
        devKey: String
    ): AppsData {
        var appsFlyerCampaign = "null"
        val appsFlyerData =  collectAppsFlyerData(appsFlyerData(activity = activity, devKey = devKey))
        val campaign = Facebook(activity).deepLink()
        val str = StringBuilder()
        var afStatus = "null"
        appsFlyerData.forEach { key ->
            if (key.key == AppConst.CAMPAIGN) {
                if (campaign != null) {
                    appsFlyerCampaign = campaign.substringAfter("Oi8v".decrypt())
                    val encodedCampaign = URLEncoder.encode(campaign, AppConst.UTF)
                    str.append("&${AppConst.CAMPAIGN}=$encodedCampaign")
                } else {
                    appsFlyerCampaign = key.value ?: "null"
                    str.append("&${key.key}=${key.value}")
                }
            } else if (key.key == AppConst.AF_STATUS) {
                afStatus = key.value ?: "null"
                str.append("&${key.key}=${key.value}")
            }


            if (key.key != AppConst.CAMPAIGN) {
                str.append("&${key.key}=${key.value}")
            } else if (campaign != null) {
                appsFlyerCampaign = campaign.substringAfter("Oi8v".decrypt())
                val encodedCampaign = URLEncoder.encode(campaign, AppConst.UTF)
                str.append("&${AppConst.CAMPAIGN}=$encodedCampaign")
            } else {
                appsFlyerCampaign = appsFlyerData[key.key].toString()
                str.append("&${key.key}=${key.value}")
            }
        }
        return AppsData(info = str.toString(), campaign = appsFlyerCampaign, afStatus = afStatus)
    }

    private fun collectAppsFlyerData(data: Map<String, Any>?): HashMap<String, String?> {
        val hashMap = HashMap<String, String?>()
        keys.forEach { key ->
            val value = URLEncoder.encode(data?.get(key).toString(), AppConst.UTF)
            hashMap[key] = value
        }
        return hashMap
    }
}