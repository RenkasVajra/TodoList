package com.example.myapplication

import android.content.Context
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException

class FileStorage(private val context: Context) {

    private val fileName = "todo_items.json"

    fun saveTodoItems(items: List<TodoItem>) {
        val jsonArray = JSONArray()
        for (item in items) {
            jsonArray.put(item.toJsonObject())
        }

        try {
            val fileOutputStream: FileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            fileOutputStream.write(jsonArray.toString().toByteArray())
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadTodoItems(): List<TodoItem> {
        val items = mutableListOf<TodoItem>()
        try {
            val file = File(context.filesDir, fileName)
            if (!file.exists()) return items

            val jsonString = FileReader(file).readText()
            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val todoItem = jsonObject.toTodoItem()
                if (todoItem != null) {
                    items.add(todoItem)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return items
    }
}