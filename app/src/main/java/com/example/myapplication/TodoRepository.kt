package com.example.myapplication

import android.content.Context
import com.example.myapplication.database.RoomCache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.slf4j.LoggerFactory


interface TodoRepository {

    fun getAllTodos(): Flow<List<TodoItem>>

    fun getTodoById(uid: String): Flow<TodoItem?>

    suspend fun saveTodo(todoItem: TodoItem)

    suspend fun deleteTodo(uid: String)

    suspend fun syncWithBackend()
}

class TodoRepositoryImpl(
    private val context: Context,
    private val roomCache: RoomCache,
    private val networkApi: NetworkApi
) : TodoRepository {

    private val logger = LoggerFactory.getLogger(TodoRepositoryImpl::class.java)

    override fun getAllTodos(): Flow<List<TodoItem>> = flow {
        try {
            try {
                val backendTodos = networkApi.loadTodosFromBackend()

                roomCache.saveTodos(backendTodos)
                emit(backendTodos)
                logger.info("Загружены актуальные данные из бэкенда")
            } catch (e: Exception) {
                logger.warn("Не удалось загрузить из бэкенда, используем кэш", e)

                roomCache.getAllTodosFlow().collect { cachedTodos ->
                    emit(cachedTodos)
                }
            }
        } catch (e: Exception) {
            logger.error("Ошибка при загрузке задач", e)
            emit(emptyList())
        }
    }

    override fun getTodoById(uid: String): Flow<TodoItem?> = roomCache.getTodoByIdFlow(uid)

    override suspend fun saveTodo(todoItem: TodoItem) {
        try {
            logger.info("TodoRepository: Сохранение задачи ${todoItem.uid} - ${todoItem.text}")
            networkApi.sendTodoToBackend(todoItem)
            roomCache.saveTodo(todoItem)
            logger.info("Задача ${todoItem.uid} сохранена на сервере и в Room кэше")
        } catch (e: Exception) {
            logger.error("TodoRepository: Ошибка при сохранении задачи ${todoItem.uid}: ${e.message}", e)
            throw e
        }
    }

    override suspend fun deleteTodo(uid: String) {
        try {
            networkApi.deleteTodoFromBackend(uid)
            roomCache.deleteTodo(uid)
            logger.info("Задача $uid удалена с сервера и из Room кэша")
        } catch (e: Exception) {
            logger.error("Ошибка при удалении задачи $uid", e)
            throw e
        }
    }

    override suspend fun syncWithBackend() {
        try {
            logger.info("Перезагрузка")

            val backendTodos = networkApi.loadTodosFromBackend()
            val cachedTodos = roomCache.getAllTodos()

            cachedTodos.forEach { cachedTodo ->
                if (backendTodos.none { it.uid == cachedTodo.uid }) {
                    roomCache.deleteTodo(cachedTodo.uid)
                }
            }

            roomCache.saveTodos(backendTodos)

            logger.info("Перезагрузка завершена, загружено ${backendTodos.size} задач")
        } catch (e: Exception) {
            logger.error("Ошибка при перезагрузке с бэкенда", e)
            throw e
        }
    }
}
