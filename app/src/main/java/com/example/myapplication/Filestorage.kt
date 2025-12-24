package com.example.myapplication

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import org.json.JSONArray
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.*

@RequiresApi(Build.VERSION_CODES.O)
class FileStorage(private val context: Context) {

    private val logger = LoggerFactory.getLogger(FileStorage::class.java)
    private val todosFile = File(context.filesDir, "todos.json")

    init {
        if (!todosFile.exists()) {
            saveTodosToFile(emptyList())
        }
    }

    fun loadAllTodos(): List<TodoItem> {
        return try {
            val jsonString = todosFile.readText()
            if (jsonString.isBlank()) {
                emptyList()
            } else {
                val jsonArray = JSONArray(jsonString)
                val todos = mutableListOf<TodoItem>()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    jsonObject.toTodoItem()?.let { todos.add(it) }
                }
                todos
            }
        } catch (e: Exception) {
            logger.error("Ошибка при загрузке всех задач", e)
            emptyList()
        }
    }

    fun loadTodoById(uid: String): TodoItem? {
        return loadAllTodos().find { it.uid == uid }
    }

    fun saveTodo(todoItem: TodoItem) {
        try {
            val todos = loadAllTodos().toMutableList()

            todos.removeIf { it.uid == todoItem.uid }

            todos.add(todoItem)

            saveTodosToFile(todos)
            logger.debug("Задача {} сохранена в кэш", todoItem.uid)
        } catch (e: Exception) {
            logger.error("Ошибка при сохранении задачи ${todoItem.uid}", e)
            throw e
        }
    }

    fun deleteTodo(uid: String) {
        try {
            val todos = loadAllTodos().toMutableList()
            val removed = todos.removeIf { it.uid == uid }

            if (removed) {
                saveTodosToFile(todos)
                logger.debug("Задача {} удалена из кэша", uid)
            } else {
                logger.warn("Задача {} не найдена для удаления", uid)
            }
        } catch (e: Exception) {
            logger.error("Ошибка при удалении задачи $uid", e)
            throw e
        }
    }

    private fun saveTodosToFile(todos: List<TodoItem>) {
        try {
            val jsonArray = JSONArray()
            todos.forEach { todo ->
                jsonArray.put(todo.toJson())
            }
            todosFile.writeText(jsonArray.toString())
        } catch (e: Exception) {
            logger.error("Ошибка при сохранении задач в файл", e)
            throw e
        }
    }

    fun exportTasksToFile(todos: List<TodoItem>): String? {
        val filename = "todo_list.txt"
        val file = File(context.filesDir, filename)
        return try {
            BufferedWriter(FileWriter(file)).use { writer ->
                for (todo in todos) {
                    writer.write("${todo.uid},${todo.text},${todo.importance},${todo.deadline}\n")
                }
            }
            file.absolutePath
        } catch (e: IOException) {
            logger.error("Ошибка при экспорте задач", e)
            null
        }
    }

    fun addTodoItem(item: TodoItem) {
        saveTodo(item)
    }

    fun removeTodoItem(uid: String) {
        deleteTodo(uid)
    }
}
