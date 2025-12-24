package com.example.myapplication.database

import android.content.Context
import com.example.myapplication.TodoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory


class RoomCache(context: Context) {

    private val logger = LoggerFactory.getLogger(RoomCache::class.java)
    private val database = AppDatabase.getDatabase(context)
    private val todoDao = database.todoDao()

    fun getAllTodosFlow(): Flow<List<TodoItem>> {
        return todoDao.getAllTodosFlow().map { entities ->
            entities.map { it.toTodoItem() }
        }
    }


    suspend fun getAllTodos(): List<TodoItem> {
        return todoDao.getAllTodos().map { it.toTodoItem() }
    }

    fun getTodoByIdFlow(uid: String): Flow<TodoItem?> {
        return todoDao.getTodoByIdFlow(uid).map { entity ->
            entity?.toTodoItem()
        }
    }


    suspend fun getTodoById(uid: String): TodoItem? {
        return todoDao.getTodoById(uid)?.toTodoItem()
    }

    suspend fun saveTodo(todoItem: TodoItem) {
        try {
            val entity = todoItem.toTodoEntity()
            todoDao.insertTodo(entity)
            logger.debug("Задача {} сохранена в Room кэше ", todoItem.uid)
        } catch (e: Exception) {
            logger.error("Ошибка при сохранении задачи  в Room кэш\n", e)
            throw e
        }
    }


    suspend fun saveTodos(todoItems: List<TodoItem>) {
        try {
            val entities = todoItems.map { it.toTodoEntity() }
            todoDao.insertTodos(entities)
            logger.debug("Сохранено {} задач в Room кэш\n", todoItems.size)
        } catch (e: Exception) {
            logger.error("Ошибка при сохранении списка задач в Room\n", e)
            throw e
        }
    }


    suspend fun updateTodo(todoItem: TodoItem) {
        try {
            val entity = todoItem.toTodoEntity()
            todoDao.updateTodo(entity)
            logger.debug("Задача {} обновлена в Room кэше \n", todoItem.uid)
        } catch (e: Exception) {
            logger.error("Ошибка при обновлении задачи  в Room кэше\n", e)
            throw e
        }
    }


    suspend fun updateTodoDoneStatus(uid: String, isDone: Boolean) {
        try {
            todoDao.updateTodoDoneStatus(uid, isDone)
            logger.debug("Статус задачи {} обновлен на {} в Room кэш\n", uid, isDone)
        } catch (e: Exception) {
            logger.error("Ошибка при обновлении статуса задачи  в Room\n", e)
            throw e
        }
    }


    suspend fun deleteTodo(uid: String) {
        try {
            val deletedRows = todoDao.deleteTodoById(uid)
            if (deletedRows > 0) {
                logger.debug("Задача {} удалена из Room кэша\n", uid)
            } else {
                logger.warn("Задача {} не найдена в Room кэше для удаления\n", uid)
            }
        } catch (e: Exception) {
            logger.error("Ошибка при удалении задачи из Room\n", e)
            throw e
        }
    }


    suspend fun clearAllCache() {
        try {
            todoDao.deleteAllTodos()
            logger.info("Room кэш очищен\n")
        } catch (e: Exception) {
            logger.error("Ошибка при очистке Room кэша", e)
            throw e
        }
    }

    suspend fun getTodosCount(): Long {
        return try {
            todoDao.getTodosCount()
        } catch (e: Exception) {
            logger.error("Ошибка при получении количества задач в Room\n", e)
            0L
        }
    }


    fun getUncompletedTodosFlow(): Flow<List<TodoItem>> {
        return todoDao.getUncompletedTodosFlow().map { entities ->
            entities.map { it.toTodoItem() }
        }
    }


    fun getCompletedTodosFlow(): Flow<List<TodoItem>> {
        return todoDao.getCompletedTodosFlow().map { entities ->
            entities.map { it.toTodoItem() }
        }
    }


    fun getTodosByImportanceFlow(importance: String): Flow<List<TodoItem>> {
        return todoDao.getTodosByImportanceFlow(importance).map { entities ->
            entities.map { it.toTodoItem() }
        }
    }


    fun getTodosWithDeadlineFlow(): Flow<List<TodoItem>> {
        return todoDao.getTodosWithDeadlineFlow().map { entities ->
            entities.map { it.toTodoItem() }
        }
    }


    suspend fun clearOldCompletedTodos() {
        try {
            val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
            val deletedCount = todoDao.deleteOldCompletedTodos(thirtyDaysAgo)
            if (deletedCount > 0) {
                logger.info("Удалено  старых выполненных задач из Room кэша\n")
            }
        } catch (e: Exception) {
            logger.error("Ошибка при очистке старых задач из Room\n", e)
        }
    }
}
