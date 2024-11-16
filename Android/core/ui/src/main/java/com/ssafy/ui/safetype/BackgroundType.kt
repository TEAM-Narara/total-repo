package com.ssafy.ui.safetype

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.ssafy.model.background.Cover
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val coverType = object : NavType<Cover?>(
    isNullableAllowed = true
) {
    override fun put(bundle: Bundle, key: String, value: Cover?) {
        value?.let { bundle.putString(key, Json.encodeToString(it)) }
    }

    override fun get(bundle: Bundle, key: String): Cover? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun serializeAsValue(value: Cover?): String {
        return Uri.encode(Json.encodeToString(value))
    }

    override fun parseValue(value: String): Cover? {
        return try {
            Json.decodeFromString<Cover>(Uri.decode(value))
        } catch (e: Exception) {
            null
        }
    }
}
