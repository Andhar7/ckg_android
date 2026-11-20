package com.andrejkling.ckg.model

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class Book(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val author: String,
    val synopsis: String = ""
)

// Books saved on the phone as a JSON file (the iOS app uses SwiftData).
class BooksStore(private val context: Context) {

    private val file get() = java.io.File(context.filesDir, "books.json")

    fun load(): List<Book> {
        if (!file.exists()) return emptyList()
        val array = JSONArray(file.readText())
        return (0 until array.length()).map {
            val o = array.getJSONObject(it)
            Book(
                id = o.getString("id"),
                title = o.getString("title"),
                author = o.getString("author"),
                synopsis = o.optString("synopsis", "")
            )
        }
    }

    fun save(books: List<Book>) {
        val array = JSONArray()
        books.forEach { b ->
            array.put(
                JSONObject()
                    .put("id", b.id)
                    .put("title", b.title)
                    .put("author", b.author)
                    .put("synopsis", b.synopsis)
            )
        }
        file.writeText(array.toString())
    }
}
