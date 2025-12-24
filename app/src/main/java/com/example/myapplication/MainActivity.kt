package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import android.graphics.Color
import android.widget.Toast
import androidx.compose.ui.tooling.preview.Preview
import ch.qos.logback.classic.LoggerContext
import org.slf4j.LoggerFactory
import kotlinx.coroutines.launch




class MainActivity : ComponentActivity() {
    private val logger = LoggerFactory.getLogger(MainActivity::class.java)
    private val todoListManager = TodoListManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            todoListManager.startAutoCleanup()
            val context = LoggerFactory.getILoggerFactory() as LoggerContext
            context.reset()
            logger.info("MainActivity: onCreate()")

        setContent {
            MyApplicationTheme {
                MainContent()
            }
        }
        } catch (e: Exception) {
            logger.error("Ошибка при инициализации MainActivity", e)
            finish()
        }
    }
}

@Composable
fun MainContent() {
    val context = LocalContext.current
    val viewModel = remember { TodoViewModel.create(context) }

    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "task_list"
    ) {
        composable("task_list") {
            TaskListScreen(
                uiState = uiState,
                onTaskClick = { task ->
                    navController.navigate("edit_task/${task.uid}")
                },
                onAddTaskClick = {
                    navController.navigate("edit_task/new")
                },
                onDeleteTask = { task ->
                    viewModel.deleteTodo(task.uid)
                },
                onSaveTask = { task ->
                    viewModel.saveTodo(task)
                },
                onSyncClick = {
                    viewModel.syncWithBackend()
                }
            )
        }
        
        composable(
            route = "edit_task/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: "new"
            val task = if (taskId == "new") {
                null
            } else {
                uiState.todos.find { it.uid == taskId }
            }

            EditTaskScreen(
                todoItem = task,
                navController = navController,
                saveChanges = { updatedTask ->
                    viewModel.saveTodo(updatedTask)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TaskListScreen(
    uiState: TodoUiState,
    onTaskClick: (TodoItem) -> Unit,
    onAddTaskClick: () -> Unit,
    onDeleteTask: (TodoItem) -> Unit,
    onSaveTask: (TodoItem) -> Unit,
    onSyncClick: () -> Unit
)
{
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    uiState.error?.let { error ->
        LaunchedEffect(error) {
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        floatingActionButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = {
                        if (uiState.todos.isNotEmpty()) {
                            scope.launch {
                                try {
                                    val filename = "todo_list_export.txt"
                                    val file = java.io.File(context.filesDir, filename)
                                    file.bufferedWriter().use { writer ->
                                        uiState.todos.forEach { todo ->
                                            writer.write("${todo.uid},${todo.text},${todo.importance},${todo.deadline ?: "нет дедлайна"}\n")
                                        }
                                    }
                                    Toast.makeText(context, "Файл сохранён: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Ошибка при сохранении файла: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.padding(16.dp),
                    enabled = uiState.todos.isNotEmpty()
                ) {
                    Text("Скачать список")
                }

                FloatingActionButton(
                    onClick = onAddTaskClick,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Добавить задачу"
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Задача",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Важность",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Дедлайн",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when {
                            !uiState.isApiConfigured -> Icons.Default.Delete
                            uiState.isOnline -> Icons.Default.Add
                            else -> Icons.Default.Delete
                        },
                        contentDescription = when {
                            !uiState.isApiConfigured -> "API не настроен"
                            uiState.isOnline -> "Онлайн"
                            else -> "Оффлайн"
                        },
                        tint = when {
                            !uiState.isApiConfigured -> androidx.compose.ui.graphics.Color.Gray
                            uiState.isOnline -> androidx.compose.ui.graphics.Color.Green
                            else -> androidx.compose.ui.graphics.Color.Red
                        }
                    )
                    IconButton(onClick = onSyncClick) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Перезагрузить данные"
                        )
                    }
                }
            }

            if (uiState.todos.isEmpty() && !uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Список задач пуст",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = uiState.todos,
                        key = { it.uid }
                    ) { task ->
                        SwipeableTaskItem(
                            task = task,
                            onTaskClick = { onTaskClick(task) },
                            onDelete = { onDeleteTask(task) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableTaskItem(
    task: TodoItem,
    onTaskClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = MaterialTheme.colorScheme.onError
                    )
                    Text(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.onError,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        },
        dismissContent = {
            TaskItemRow(
                task = task,
                onClick = onTaskClick
            )
        },
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    )
}

@Composable
fun TaskItemRow(
    task: TodoItem,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onClick() }
            .background(
                color = if (task.color != null) {
                    ComposeColor(task.color)
                } else {
                    MaterialTheme.colorScheme.surface
                },
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = task.text,
            style = MaterialTheme.typography.bodyMedium.copy(
                textDecoration = if (task.isDone) TextDecoration.LineThrough else null
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task.importance.name,
                style = MaterialTheme.typography.bodySmall,
                color = when (task.importance) {
                    Importance.Важная -> MaterialTheme.colorScheme.error
                    Importance.Неважная -> MaterialTheme.colorScheme.outline
                    Importance.Обычная -> MaterialTheme.colorScheme.onSurface
                }
            )

            Text(
                text = task.deadline ?: "—",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 411,
    heightDp = 891,
)
@Composable
@OptIn(ExperimentalMaterialApi::class)
fun PreviewTaskListScreen() {
    MyApplicationTheme {
        val testTodos = remember {
            mutableStateListOf(
                TodoItem(
                    uid = "1",
                    text = "Купить продукты",
                    importance = Importance.Важная,
                    color = Color.YELLOW,
                    deadline = "25-12-2025",
                    isDone = false
                ),
                TodoItem(
                    uid = "2",
                    text = "Выполнить домашнее заданиеВыполнить домашнее заданиеВыполнить домашнее заданиеВыполнить домашнее заданиеВыполнить домашнее заданиеВыполнить домашнее задание",
                    importance = Importance.Обычная,
                    color = null,
                    deadline = "30-12-2025",
                    isDone = false
                ),
                TodoItem(
                    uid = "3",
                    text = "Позвонить другу",
                    importance = Importance.Неважная,
                    color = Color.GREEN,
                    deadline = null,
                    isDone = false
                ),
                TodoItem(
                    uid = "4",
                    text = "Закончить проект",
                    importance = Importance.Важная,
                    color = Color.RED,
                    deadline = "28-12-2025",
                    isDone = true
                ),
                TodoItem(
                    uid = "5",
                    text = "Прочитать книгу",
                    importance = Importance.Обычная,
                    color = null,
                    deadline = "11-11-2027",
                    isDone = false
                ),
                TodoItem(
                    uid = "6",
                    text = "Прочитать книгу2",
                    importance = Importance.Обычная,
                    color = null,
                    deadline = "31-12-2024",
                    isDone = false
                ),
                TodoItem(
                    uid = "7",
                    text = "Прочитать книгу3",
                    importance = Importance.Обычная,
                    color = null,
                    deadline = "31-12-2024",
                    isDone = false
                ),
                TodoItem(
                    uid = "8",
                    text = "Прочитать книгу4",
                    importance = Importance.Обычная,
                    color = null,
                    deadline = "31-12-2024",
                    isDone = false
                ),
                TodoItem(
                    uid = "9",
                    text = "Прочитать книгу5",
                    importance = Importance.Обычная,
                    color = null,
                    deadline = "31-12-2024",
                    isDone = false
                ),
                TodoItem(
                    uid = "10",
                    text = "Прочитать книгу6",
                    importance = Importance.Обычная,
                    color = null,
                    deadline = "31-12-2025",
                    isDone = false
                ),
                TodoItem(
                    uid = "11",
                    text = "Прочитать книгу7",
                    importance = Importance.Обычная,
                    color = null,
                    deadline = "31-12-2024",
                    isDone = false
                ),
                TodoItem(
                    uid = "12",
                    text = "Прочитать книгу8",
                    importance = Importance.Обычная,
                    color = null,
                    deadline = "31-12-2024",
                    isDone = false
                )
            )
        }

        TaskListScreen(
            uiState = TodoUiState(todos = testTodos),
            onTaskClick = { },
            onAddTaskClick = { },
            onDeleteTask = { },
            onSaveTask = { },
            onSyncClick = { }
        )
    }
}
