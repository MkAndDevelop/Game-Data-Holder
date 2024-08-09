package com.game.data.holder.sdk.facebook

import android.content.Context
import com.facebook.FacebookSdk
import com.facebook.applinks.AppLinkData
import com.game.data.holder.LibData
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FacebookImplementation(private val context: Context) : FacebookRepository {

    @Suppress("DEPRECATION")
    private fun initFacebookSdk() {
        FacebookSdk.apply {
            setApplicationId(LibData.id)
            setClientToken(LibData.token)
            sdkInitialize(context)
            setAdvertiserIDCollectionEnabled(true)
            setAutoInitEnabled(true)
            fullyInitialize()
        }
    }


    override fun referrer(): String? {
        initFacebookSdk()
        val sp = context.getSharedPreferences("com.facebook.sdk.appEventPreferences", 0)
        return sp.getString("install_referrer", "empty")
    }
}