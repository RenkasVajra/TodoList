package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import java.util.UUID
import androidx.core.content.edit
import org.slf4j.LoggerFactory


@RequiresApi(Build.VERSION_CODES.O)
class FileStorage(private val context: Context) {
    private val logger = LoggerFactory.getLogger(FileStorage::class.java)
    init { logger.info("FileStorage: Initializing FileStorage") }
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("TodoPrefs", Context.MODE_PRIVATE)
    private val _items = mutableListOf<TodoItem>()
    val items: List<TodoItem> = _items.toList()
    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    init { loadTasksFromPreferences() }


    fun removeOverdueTasks() {
        val now = LocalDateTime.now()
        _items.removeIf { item ->
            item.deadline?.let { deadlineString ->
                try {
                    val deadline = LocalDateTime.parse(deadlineString, formatter)
                    deadline.isBefore(now)
                } catch (e: DateTimeParseException) { false }
            } ?: false
        }
        saveTasksToPreferences()
    }

    private fun saveTasksToPreferences() {
        sharedPreferences.edit {
            val jsonArray = _items.map { it.toJson() }.toString()
            putString("todoItems", jsonArray)
        }
    }

    private fun loadTasksFromPreferences() {
        val jsonString = sharedPreferences.getString("todoItems", "[]") ?: "[]"
        try {
            val jsonArray = org.json.JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                jsonObject.toTodoItem()?.let { _items.add(it) }
            }
        } catch (e: Exception) {  e.printStackTrace() }
        removeOverdueTasks()
    }

    fun addTodoItem(item: TodoItem ) {
        _items.add(item)
        saveTasksToPreferences()
        logger.debug("Задача {} добавлена", item.uid)
    }

    fun removeTodoItem(uid: String) {
        _items.removeAll { it.uid == uid }
        saveTasksToPreferences()
        logger.debug("Задача {} удалена", uid)
    }
}
