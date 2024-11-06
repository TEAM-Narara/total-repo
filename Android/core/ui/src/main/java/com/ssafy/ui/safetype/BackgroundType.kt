package com.ssafy.ui.safetype

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.ssafy.model.board.Background
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val backgroundType = object : NavType<Background?>(
    isNullableAllowed = true
) {
    override fun put(bundle: Bundle, key: String, value: Background?) {
        value?.let { bundle.putString(key, Json.encodeToString(it)) }
    }

    override fun get(bundle: Bundle, key: String): Background? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun serializeAsValue(value: Background?): String {
        return Uri.encode(Json.encodeToString(value))
    }

    override fun parseValue(value: String): Background? {
        return try {
            Json.decodeFromString<Background>(Uri.decode(value))
        } catch (e: Exception) {
            null
        }
    }
}
