package com.game.data.holder.sdk.facebook

import android.content.Context
import com.facebook.FacebookSdk
import com.game.data.holder.LibData
import kotlinx.coroutines.delay

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


    override suspend fun referrer(): String? {
        initFacebookSdk()
        delay(3000)
        val sp = context.getSharedPreferences("com.facebook.sdk"+".appEventPreferences", 0)
        return sp.getString("install_"+"referrer", null)
    }
}