package com.sinyee.babybus.simpleurlbuilderfinalmoderation.sdk.referrer

import android.content.Context
import com.sinyee.babybus.simpleurlbuilderfinalmoderation.sdk.Facebook
import com.sinyee.babybus.simpleurlbuilderfinalmoderation.utils.AppConst
import com.sinyee.babybus.simpleurlbuilderfinalmoderation.utils.decrypt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URLDecoder
import java.net.URLEncoder
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class ReferrerNoAppsData(private val context: Context) {

    private var defaultMap = hashMapOf(
        Pair(AppConst.CAMPAIGN, "null"),
        Pair(AppConst.MEDIA_SOURCE, "null"),
        Pair(AppConst.AF_CHANNEL, "null"),
        Pair(AppConst.CAMPAIGN_ID, "null"),
        Pair(AppConst.AD_ID, "null"),
        Pair(AppConst.ADSET, "null"),
        Pair(AppConst.AF_STATUS, "null")
    )
    private val nullData = ReferrerInfo(accountId = "&${AppConst.ACCOUNT_ID}=null", info = defaultMap)

    suspend fun getReferrerData(fbKey: String): ReferrerData {
        var referrerCampaign: String? = null
        val referrerData = referrerData(fbKey)
        val referrerInfo = referrerData.info
        val campaign = Facebook(context).deepLink()
        val str = StringBuilder()
        referrerInfo.forEach { key ->
            if (key.key != AppConst.CAMPAIGN) {
                str.append("&${key.key}=${key.value}")
            } else if (campaign != null) {
                referrerCampaign = campaign.substringAfter("://")
                val encodedCampaign = URLEncoder.encode(campaign, AppConst.UTF)
                str.append("&${AppConst.CAMPAIGN}=$encodedCampaign")
            } else {
                referrerCampaign = referrerInfo[key.key].toString()
                str.append("&${key.key}=${key.value}")
            }
        }
        return ReferrerData(
            info = str.toString(),
            campaign = referrerCampaign,
            accountId = referrerData.accountId
        )
    }

    private suspend fun referrerData(fbKey: String): ReferrerInfo = suspendCoroutine {
        CoroutineScope(Dispatchers.IO).launch {
            it.resume(decryptNoApps(fbKey))
        }
    }

    private suspend fun decryptNoApps(fbKey: String): ReferrerInfo {
        val referrer = SetUpRef(context).getRef()
        val decodeReferrer = try {
            URLDecoder.decode(referrer, AppConst.UTF)
        } catch (e: Exception) {
            return nullData
        }
        if (!decodeReferrer.contains(AppConst.UTM_CONTENT)) return nullData
        else try {
            val urlForDecode = decodeReferrer.split("${AppConst.UTM_CONTENT}=")[1]
            val jsonURL = JSONObject(urlForDecode)
            val source = JSONObject(jsonURL[AppConst.SOURCE].toString())
            val data = source[AppConst.DATA]
            val nonce = source[AppConst.NONCE]
            val message = decodeHex(data.toString())
            val secretKeyFbReferrer = decodeHex(nonce.toString())
            val nonceSpec = IvParameterSpec(secretKeyFbReferrer)
            val specKey = SecretKeySpec(decodeHex(fbKey), AppConst.AES)
            val cipher = Cipher.getInstance(AppConst.AES)
            cipher.init(Cipher.DECRYPT_MODE, specKey, nonceSpec)
            val result = JSONObject(String(cipher.doFinal(message)))

            //val hashMap: LinkedHashMap<String, String> = LinkedHashMap()
            var mediaSource = "RmFjZWJvb2tBZHM=".decrypt()
            var afChannel = "RmFjZWJvb2s=".decrypt()
            val accountId = "&${AppConst.ACCOUNT_ID}=${result.getString(AppConst.ACCOUNT_ID)}"
            val adId = result.getString(AppConst.AD_ID)
            val adset = result.getString("YWRncm91cF9uYW1l".decrypt())
            val campaignId = result.getString(AppConst.CAMPAIGN_ID)
            val campaign = result.getString("Y2FtcGFpZ25fZ3JvdXBfbmFtZQ==".decrypt())
            val isInstagram = result.getString("aXNfaW5zdGFncmFt".decrypt()).toBoolean()
            if (isInstagram) {
                mediaSource = "SW5zdGFncmFt".decrypt()
                afChannel = "SW5zdGFncmFt".decrypt()
            }
            defaultMap[AppConst.CAMPAIGN] = campaign
            //hashMap[AppConst.CAMPAIGN] = campaign
            defaultMap[AppConst.MEDIA_SOURCE] = mediaSource
            //hashMap[AppConst.MEDIA_SOURCE] = mediaSource
            defaultMap[AppConst.AF_CHANNEL] = afChannel
            //hashMap[AppConst.AF_CHANNEL] = afChannel
            defaultMap[AppConst.CAMPAIGN_ID] = campaignId
            //hashMap[AppConst.CAMPAIGN_ID] = campaignId
            defaultMap[AppConst.AD_ID] = adId
            //hashMap[AppConst.AD_ID] = adId
            defaultMap[AppConst.ADSET] = adset
            //hashMap[AppConst.ADSET] = adset

            return ReferrerInfo(accountId = accountId, info = defaultMap)
        } catch (e: Exception) {
            return nullData
        }
    }
    private fun decodeHex(string: String): ByteArray = string.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}

data class ReferrerData(val info: String?, val campaign: String?, val accountId: String)