package com.sinyee.babybus.simpleurlbuilderfinalmoderation.utils

import android.util.Base64

internal fun String.decrypt(): String {
    val decodedBytes = Base64.decode(this, Base64.URL_SAFE)
    return String(decodedBytes)
}