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
            logger.debug("Р—Р°РґР°С‡Р° {} СЃРѕС…СЂР°РЅРµРЅР° РІ Room РєСЌС€", todoItem.uid)
        } catch (e: Exception) {
            logger.error("РћС€РёР±РєР° РїСЂРё СЃРѕС…СЂР°РЅРµРЅРёРё Р·Р°РґР°С‡Рё  РІ Room", e)
            throw e
        }
    }


    suspend fun saveTodos(todoItems: List<TodoItem>) {
        try {
            val entities = todoItems.map { it.toTodoEntity() }
            todoDao.insertTodos(entities)
            logger.debug("РЎРѕС…СЂР°РЅРµРЅРѕ {} Р·Р°РґР°С‡ РІ Room РєСЌС€", todoItems.size)
        } catch (e: Exception) {
            logger.error("РћС€РёР±РєР° РїСЂРё СЃРѕС…СЂР°РЅРµРЅРёРё СЃРїРёСЃРєР° Р·Р°РґР°С‡ РІ Room", e)
            throw e
        }
    }


    suspend fun updateTodo(todoItem: TodoItem) {
        try {
            val entity = todoItem.toTodoEntity()
            todoDao.updateTodo(entity)
            logger.debug("Р—Р°РґР°С‡Р° {} РѕР±РЅРѕРІР»РµРЅР° РІ Room РєСЌС€", todoItem.uid)
        } catch (e: Exception) {
            logger.error("РћС€РёР±РєР° РїСЂРё РѕР±РЅРѕРІР»РµРЅРёРё Р·Р°РґР°С‡Рё  РІ Room", e)
            throw e
        }
    }


    suspend fun updateTodoDoneStatus(uid: String, isDone: Boolean) {
        try {
            todoDao.updateTodoDoneStatus(uid, isDone)
            logger.debug("РЎС‚Р°С‚СѓСЃ Р·Р°РґР°С‡Рё {} РѕР±РЅРѕРІР»РµРЅ РЅР° {} РІ Room РєСЌС€", uid, isDone)
        } catch (e: Exception) {
            logger.error("РћС€РёР±РєР° РїСЂРё РѕР±РЅРѕРІР»РµРЅРёРё СЃС‚Р°С‚СѓСЃР° Р·Р°РґР°С‡Рё  РІ Room", e)
            throw e
        }
    }


    suspend fun deleteTodo(uid: String) {
        try {
            val deletedRows = todoDao.deleteTodoById(uid)
            if (deletedRows > 0) {
                logger.debug("Р—Р°РґР°С‡Р° {} СѓРґР°Р»РµРЅР° РёР· Room РєСЌС€Р°", uid)
            } else {
                logger.warn("Р—Р°РґР°С‡Р° {} РЅРµ РЅР°Р№РґРµРЅР° РІ Room РєСЌС€Рµ РґР»СЏ СѓРґР°Р»РµРЅРёСЏ", uid)
            }
        } catch (e: Exception) {
            logger.error("РћС€РёР±РєР° РїСЂРё СѓРґР°Р»РµРЅРёРё Р·Р°РґР°С‡Рё  РёР· Room", e)
            throw e
        }
    }


    suspend fun clearAllCache() {
        try {
            todoDao.deleteAllTodos()
            logger.info("Р’РµСЃСЊ Room РєСЌС€ РѕС‡РёС‰РµРЅ")
        } catch (e: Exception) {
            logger.error("РћС€РёР±РєР° РїСЂРё РѕС‡РёСЃС‚РєРµ Room РєСЌС€Р°", e)
            throw e
        }
    }

    suspend fun getTodosCount(): Long {
        return try {
            todoDao.getTodosCount()
        } catch (e: Exception) {
            logger.error("РћС€РёР±РєР° РїСЂРё РїРѕР»СѓС‡РµРЅРёРё РєРѕР»РёС‡РµСЃС‚РІР° Р·Р°РґР°С‡ РІ Room", e)
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
                logger.info("РЈРґР°Р»РµРЅРѕ  СЃС‚Р°СЂС‹С… РІС‹РїРѕР»РЅРµРЅРЅС‹С… Р·Р°РґР°С‡ РёР· Room РєСЌС€Р°")
            }
        } catch (e: Exception) {
            logger.error("РћС€РёР±РєР° РїСЂРё РѕС‡РёСЃС‚РєРµ СЃС‚Р°СЂС‹С… Р·Р°РґР°С‡ РёР· Room", e)
        }
    }
}
