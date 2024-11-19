package com.ssafy.ui.safetype

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.ssafy.model.search.SearchParameters
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val SearchParametersType = object : NavType<SearchParameters>(
    isNullableAllowed = false
) {
    override fun put(bundle: Bundle, key: String, value: SearchParameters) {
        bundle.putString(key, Json.encodeToString(value))
    }

    override fun get(bundle: Bundle, key: String): SearchParameters {
        return Json.decodeFromString(bundle.getString(key)!!)
    }

    override fun serializeAsValue(value: SearchParameters): String {
        return Uri.encode(Json.encodeToString(value))
    }

    override fun parseValue(value: String): SearchParameters {
        return Json.decodeFromString<SearchParameters>(value)
    }
}
