package com.example.myapplication

import org.slf4j.LoggerFactory


interface NetworkApi {
    suspend fun loadTodosFromBackend(): List<TodoItem>
    suspend fun sendTodoToBackend(todoItem: TodoItem)
    suspend fun deleteTodoFromBackend(uid: String)
}


class NetworkApiImpl(
    private val apiService: TodoApiService = TodoApiService.create()
) : NetworkApi {

    private val logger = LoggerFactory.getLogger(NetworkApiImpl::class.java)

    private fun isApiConfigured(): Boolean {
        return !ApiConfig.BASE_URL.contains("your-real-api-server.com")
    }
    override suspend fun loadTodosFromBackend(): List<TodoItem> {
        if (!isApiConfigured()) {
            logger.warn("API не настроен, возвращаем пустой список")
            return emptyList()
        }

        return try {
            logger.info("Загрузка задач из бэкенда")
            val response = apiService.getTodos()
            if (response.isSuccessful) { val apiResponse = response.body()
                if (apiResponse?.status == "success" && apiResponse.data != null) {
                    val todos = apiResponse.data.todos.map { it.toInternalModel() }
                    logger.info("Загружено ${todos.size} задач из бэкенда")
                    todos
                } else {
                    logger.error("Ошибка API: ${apiResponse?.message ?: "Неизвестная ошибка"}")
                    throw Exception(apiResponse?.message ?: "Ошибка загрузки задач")
                }
            } else {
                logger.error("HTTP ошибка ${response.code()}: ${response.message()}")
                throw Exception("HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            logger.error("Ошибка при загрузке задач из бэкенда", e)
            throw e
        }
    }


    override suspend fun sendTodoToBackend(todoItem: TodoItem) {
        logger.info("Попытка отправки задачи на бэкенд: ${todoItem.text}")

        if (!isApiConfigured()) {
            logger.warn("API не настроен, пропускаем отправку задачи на бэкенд")
            return
        }

        try {
            logger.info("Отправка задачи '${todoItem.text}' на бэкенд (простая схема)")
            val apiModel = todoItem.toApiModel()
            val response = apiService.createTodo(apiModel)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.status == "success") {
                    logger.info("Задача '${todoItem.uid}' успешно отправлена на бэкенд")
                } else {
                    logger.error("Ошибка API при создании задачи: ${apiResponse?.message ?: "Неизвестная ошибка"}")
                    throw Exception(apiResponse?.message ?: "Ошибка создания задачи")
                }
            } else {
                logger.error("HTTP ошибка при создании задачи ${response.code()}: ${response.message()}")
                throw Exception("HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            logger.error("Ошибка при отправке задачи '${todoItem.uid}' на бэкенд: ${e.message}", e)
            logger.error("Stack trace:", e)
            throw e
        }
    }


    override suspend fun deleteTodoFromBackend(uid: String) {
        if (!isApiConfigured()) {
            logger.warn("API не настроен, пропускаем удаление задачи с бэкенда")
            return
        }

        try {
            logger.info("Удаление задачи '$uid' с бэкенда (простая схема)")
            val response = apiService.deleteTodo(uid)

            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.status == "success") {
                    logger.info("Задача '$uid' успешно удалена с бэкенда")
                } else {
                    logger.error("Ошибка API при удалении задачи: ${apiResponse?.message ?: "Неизвестная ошибка"}")
                    throw Exception(apiResponse?.message ?: "Ошибка удаления задачи")
                }
            } else {
                logger.error("HTTP ошибка при удалении задачи ${response.code()}: ${response.message()}")
                throw Exception("HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            logger.error("Ошибка при удалении задачи '$uid' с бэкенда", e)
            throw e
        }
    }
}
