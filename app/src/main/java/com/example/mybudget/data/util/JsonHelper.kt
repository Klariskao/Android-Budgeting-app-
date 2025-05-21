package com.example.mybudget.data.util

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object JsonHelper {
    val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    inline fun <reified T> serializeToJson(value: T): String {
        return json.encodeToString(value)
    }

    inline fun <reified T> deserializeFromJson(jsonString: String): T {
        return json.decodeFromString(jsonString)
    }
}
