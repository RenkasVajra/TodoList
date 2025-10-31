package com.example.myapplication

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import org.slf4j.LoggerFactory
import java.io.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@RequiresApi(Build.VERSION_CODES.O)
class FileStorage(private val context: Context) {

    private val logger = LoggerFactory.getLogger(FileStorage::class.java)
    private val items = mutableListOf<TodoItem>()


    init {
        loadTasksFromFile()
    }


    fun addTodoItem(item: TodoItem) {
        items.add(item)
        //saveTasksToFile()
        logger.debug("Задача {} добавлена", item.uid)
    }


    fun removeTodoItem(uid: String) {
        items.removeIf { it.uid == uid }
        //saveTasksToFile()
        logger.debug("Задача {} удалена", uid)
    }


    private fun saveTasksToFile() {
        try {
            FileOutputStream(File(context.filesDir, "todos.txt")).use {
                ObjectOutputStream(it).use { it.writeObject(items) }
            }
        } catch (e: Exception) {
            logger.error("Ошибка при сохранении задач в файл", e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun loadTasksFromFile() {
        val file = File(context.filesDir, "todos.txt")

        if (file.exists()) {
            try {
                ObjectInputStream(FileInputStream(file)).use {
                    val loadedList = it.readObject() as? MutableList<TodoItem>
                    items.clear()
                    items.addAll(loadedList ?: emptyList())
                }
            } catch (e: Exception) {
                logger.error("Произошла ошибка при загрузке задач из файла", e)
            }
        }
    }
}
