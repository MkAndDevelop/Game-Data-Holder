package com.sinyee.babybus.simpleurlbuilderfinalmoderation

import android.app.Activity
import android.util.Log
import com.sinyee.babybus.simpleurlbuilderfinalmoderation.sdk.AppsFlayerDataBuilder
import com.sinyee.babybus.simpleurlbuilderfinalmoderation.sdk.referrer.ReferrerAccountId
import com.sinyee.babybus.simpleurlbuilderfinalmoderation.utils.DomenHolder
import com.sinyee.babybus.simpleurlbuilderfinalmoderation.utils.FacebookConst
import com.sinyee.babybus.simpleurlbuilderfinalmoderation.utils.decrypt

object SimpleUrlBuilderFinalModeration {

    suspend fun build(
        domen: String? = null,
        fbKey: String,
        devKey: String,
        battery: String? = null,
        facebookId: String,
        facebookToken: String,
        isDevSettings: Boolean? = null,
        context: Activity,
        isStaticFacebook: Boolean
    ): GameInfoData? {
        val deviceData = DeviceDataBuilder(
            battery = battery.toString(),
            isDevSettings = isDevSettings,
            context = context,
            devKey = devKey,
            facebookId = facebookId,
            facebookToken = facebookToken,
        ).getDeviceInfoUseCase()

        FacebookConst.setFacebookConst(id = facebookId, token = facebookToken, isStaticFacebook)

        val tracker: String = domen ?: DomenHolder.getRandomDome()

        val appsFlyerData =
            AppsFlayerDataBuilder.getAppsFlyerData(activity = context, devKey = devKey)
        val campaign = appsFlyerData.campaign
        val appsFlyerStr = appsFlyerData.info
        val afStatus = appsFlyerData.afStatus
        val referrerAccountId = ReferrerAccountId(context).accountId(fbKey)

        if (campaign == "null" && referrerAccountId == "JmFjY291bnRfaWQ9bnVsbA==".decrypt() && afStatus != "Tm9uLW9yZ2FuaWM=".decrypt()) return null

        val deviceDataStr = deviceData.info
        val afUserId = deviceData.userId
        val subsData = SubBuilder.getSubData(campaign)
        val push = subsData.gameItem
        val subsStr = subsData.gameItems
        val pushStr = "${"JnB1c2g9".decrypt()}$push"
        val url = "$tracker$referrerAccountId$appsFlyerStr$deviceDataStr$subsStr$pushStr"
        return GameInfoData(info = url, userIdInfo = afUserId, push = push)
    }
}

