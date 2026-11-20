package com.andrejkling.ckg.model

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

// Saved aphorisms, kept on the phone. Same key name as the iOS app (UserDefaults "SavedFavorites").
class FavoritesStore(context: Context) {

    private val prefs = context.getSharedPreferences("SavedFavorites", Context.MODE_PRIVATE)

    fun load(): List<Aphorism> {
        val json = prefs.getString("favorites", "[]") ?: "[]"
        val array = JSONArray(json)
        return (0 until array.length()).map {
            val o = array.getJSONObject(it)
            Aphorism(o.getInt("number"), o.getString("text"))
        }
    }

    fun save(favorites: List<Aphorism>) {
        val array = JSONArray()
        favorites.forEach { a ->
            array.put(JSONObject().put("number", a.number).put("text", a.text))
        }
        prefs.edit().putString("favorites", array.toString()).apply()
    }
}
