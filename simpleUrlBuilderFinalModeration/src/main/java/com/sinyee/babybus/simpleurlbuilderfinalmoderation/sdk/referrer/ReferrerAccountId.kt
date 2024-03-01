package com.sinyee.babybus.simpleurlbuilderfinalmoderation.sdk.referrer

import android.content.Context
import com.sinyee.babybus.simpleurlbuilderfinalmoderation.utils.AppConst
import org.json.JSONObject
import java.net.URLDecoder
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

internal class ReferrerAccountId(private val context: Context) {

    suspend fun accountId(fbKey: String): String = "&${AppConst.ACCOUNT_ID}=${decrypt(fbKey)}"

    private suspend fun decrypt(fbKey: String): String? {
        val referrer = SetUpRef(context).getRef()
        val decodeReferrer = try {
            URLDecoder.decode(referrer, AppConst.UTF)
        } catch (e: Exception) {
            return null
        }
        if (!decodeReferrer.contains(AppConst.UTM_CONTENT)) return null
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
            val accountId = result.get(AppConst.ACCOUNT_ID)
            return accountId.toString()
        } catch (e: Exception) {
            return null
        }
    }

    private fun decodeHex(string: String): ByteArray = string.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}

data class ReferrerInfo(val accountId: String, val info: HashMap<String, String>)