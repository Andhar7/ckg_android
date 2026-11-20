package com.andrejkling.ckg.model

import android.content.Context
import org.json.JSONArray
import java.time.LocalDate

data class Aphorism(val number: Int, val text: String)

object AphorismStore {

    /// Reads madal.json (a JSON array of "2601. text" strings), like the iOS Aphorism.loadAphorisms()
    fun load(context: Context): List<Aphorism> {
        val json = context.assets.open("madal.json").bufferedReader().use { it.readText() }
        val array = JSONArray(json)
        return (0 until array.length()).map { parse(array.getString(it)) }
    }

    /// Splits "2623. I love..." into number and text, like the iOS Aphorism.parse()
    fun parse(raw: String): Aphorism {
        val dot = raw.indexOf(". ")
        if (dot > 0) {
            val number = raw.substring(0, dot).toIntOrNull()
            if (number != null) return Aphorism(number, raw.substring(dot + 2))
        }
        return Aphorism(0, raw)
    }

    /// The same aphorism all day, changing at midnight. Uses the same day index as iOS
    /// (Calendar ordinality of the day in the era), so both apps show the same one.
    fun today(list: List<Aphorism>, date: LocalDate = LocalDate.now()): Aphorism? {
        if (list.isEmpty()) return null
        val dayOrdinal = date.toEpochDay() + 719163L   // 1 = 0001-01-01
        return list[(dayOrdinal % list.size).toInt()]
    }

    fun random(list: List<Aphorism>): Aphorism? = list.randomOrNull()
}
