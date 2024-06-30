package com.game.data.holder.storage

internal interface DataStoreRepository {
    suspend fun getString(key: String): String?
    fun putString(key: String, value: String)
}