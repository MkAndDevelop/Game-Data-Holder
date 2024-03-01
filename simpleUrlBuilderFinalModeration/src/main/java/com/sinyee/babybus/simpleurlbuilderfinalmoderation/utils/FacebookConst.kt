package com.sinyee.babybus.simpleurlbuilderfinalmoderation.utils

object FacebookConst {
    var facebookId: String? = null
        private set
    var facebookToken: String? = null
        private set

    fun setFacebookConst(id: String, token: String, isStatic: Boolean) {
        if (isStatic) {
            facebookId = null
            facebookToken = null
        } else {
            facebookId = id
            facebookToken = token
        }
    }
}