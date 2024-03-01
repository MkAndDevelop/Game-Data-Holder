package com.sinyee.babybus.simpleurlbuilderfinalmoderation.sdk

import android.content.Context
import com.facebook.FacebookSdk
import com.facebook.applinks.AppLinkData
import com.sinyee.babybus.simpleurlbuilderfinalmoderation.utils.FacebookConst
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class Facebook(
    private val context: Context
) {

    private fun initFacebookSdk() {
        FacebookSdk.apply {
            setAdvertiserIDCollectionEnabled(true)
            setAutoInitEnabled(true)
            fullyInitialize()
        }
    }

    @Suppress("DEPRECATION")
    private fun initFacebookSdk(id: String, token: String) {
        FacebookSdk.apply {
            setApplicationId(id)
            setClientToken(token)
            sdkInitialize(context)
            setAdvertiserIDCollectionEnabled(true)
            setAutoInitEnabled(true)
            fullyInitialize()
        }
    }

    suspend fun deepLink(): String? =
        suspendCoroutine { deepLink ->
            val id = FacebookConst.facebookId
            val token = FacebookConst.facebookToken
            if ((id != null && token != null)) initFacebookSdk(id = id, token = token)
            else initFacebookSdk()

            try {
                AppLinkData.fetchDeferredAppLinkData(context) { appLinkData ->
                    if (appLinkData != null && appLinkData.targetUri != null) {
                        val adta = appLinkData.targetUri.toString()
                        deepLink.resume(adta)
                    } else deepLink.resume(null)
                }
            } catch (e: Exception) {
                deepLink.resume(null)
            }
        }
}