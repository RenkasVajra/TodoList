package com.example.myapplication

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.database.RoomCache
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory


data class TodoUiState(
    val todos: List<TodoItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentTodo: TodoItem? = null,
    val isOnline: Boolean = true,
    val isApiConfigured: Boolean = true
)


class TodoViewModel(
    private val repository: TodoRepository
) : ViewModel() {
    private val logger = LoggerFactory.getLogger(TodoViewModel::class.java)
    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()
    init {
        loadTodos()
    }

    fun loadTodos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.getAllTodos().collect { todos ->
                    _uiState.update {
                        it.copy(
                            todos = todos,
                            isLoading = false,
                            error = null,
                            isOnline = true,
                            isApiConfigured = ApiConfig.BASE_URL.contains("your-real-api-server.com").not()
                        )
                    }
                }
            } catch (e: Exception) {
                logger.error("Ошибка при загрузке задач", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка загрузки данных. Проверьте подключение к интернету.",
                        isOnline = false
                    )
                }
            }
        }
    }

    fun loadTodoById(uid: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.getTodoById(uid).collect { todo ->
                    _uiState.update {
                        it.copy(
                            currentTodo = todo,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                logger.error("Ошибка при загрузке задачи $uid", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка при загрузке задачи: ${e.message}"
                    )
                }
            }
        }
    }


    fun saveTodo(todoItem: TodoItem) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

        try {
            repository.saveTodo(todoItem)

                val currentTodos = _uiState.value.todos.toMutableList()
                val existingIndex = currentTodos.indexOfFirst { it.uid == todoItem.uid }

                if (existingIndex >= 0) {
                    currentTodos[existingIndex] = todoItem
                } else {
                    currentTodos.add(todoItem)
                }

                _uiState.update {
                    it.copy(
                        todos = currentTodos,
                        currentTodo = todoItem,
                        isLoading = false,
                        error = null,
                        isOnline = true
                    )
                }

                logger.info("Задача ${todoItem.uid} успешно сохранена (синхронизировано с сервером)")
            } catch (e: Exception) {
                logger.error("Ошибка при сохранении задачи ${todoItem.uid}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Не удалось сохранить задачу. Проверьте подключение к интернету.",
                        isOnline = false
                    )
                }
            }
        }
    }


    fun deleteTodo(uid: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

        try {
            repository.deleteTodo(uid)

                val currentTodos = _uiState.value.todos.toMutableList()
                currentTodos.removeIf { it.uid == uid }

                _uiState.update {
                    it.copy(
                        todos = currentTodos,
                        currentTodo = if (_uiState.value.currentTodo?.uid == uid) null else _uiState.value.currentTodo,
                        isLoading = false,
                        error = null,
                        isOnline = true
                    )
                }

                logger.info("Задача $uid успешно удалена (синхронизировано с сервером)")
            } catch (e: Exception) {
                logger.error("Ошибка при удалении задачи $uid", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Не удалось удалить задачу. Проверьте подключение к интернету.",
                        isOnline = false
                    )
                }
            }
        }
    }


    fun syncWithBackend() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

        try {
            repository.syncWithBackend()
                loadTodos()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        isOnline = true
                    )
                }
                logger.info("Принудительная перезагрузка с бэкенда завершена")
            } catch (e: Exception) {
                logger.error("Ошибка при принудительной перезагрузке", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Не удалось перезагрузить данные. Проверьте подключение.",
                        isOnline = false
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearCurrentTodo() {
        _uiState.update { it.copy(currentTodo = null) }
    }

    fun setOnlineStatus(isOnline: Boolean) {
        _uiState.update { it.copy(isOnline = isOnline) }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TodoViewModel::class.java)

        fun create(context: Context): TodoViewModel {
            try {
                val roomCache = RoomCache(context)
                val networkApi = NetworkApiImpl()
                val repository = TodoRepositoryImpl(context, roomCache, networkApi)
                return TodoViewModel(repository)
            } catch (e: Exception) {
                logger.error("Ошибка при создании TodoViewModel", e)
                throw e
            }
        }
    }
}
