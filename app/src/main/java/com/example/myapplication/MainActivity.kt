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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import android.graphics.Color
import ch.qos.logback.classic.LoggerContext
import org.slf4j.LoggerFactory




class MainActivity : ComponentActivity() {
    private val logger = LoggerFactory.getLogger(MainActivity::class.java)
    private val todoListManager = TodoListManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        todoListManager.startAutoCleanup()
        val context = LoggerFactory.getILoggerFactory() as LoggerContext
        context.reset()
        logger.info("MainActivity: onCreate()")

        setContent {
            MyApplicationTheme {
                MainContent()
            }
        }
    }
}

@Composable
fun MainContent() {
    val navController = rememberNavController()
    val todos = remember { mutableStateListOf<TodoItem>() }
    
    NavHost(
        navController = navController,
        startDestination = "task_list"
    ) {
        composable("task_list") {
            TaskListScreen(
                todos = todos,
                onTaskClick = { task ->
                    navController.navigate("edit_task/${task.uid}")
                },
                onAddTaskClick = {
                    navController.navigate("edit_task/new")
                },
                onDeleteTask = { task ->
                    todos.remove(task)
                },
                onSaveTask = { task ->
                    val existingIndex = todos.indexOfFirst { it.uid == task.uid }
                    if (existingIndex >= 0) {
                        todos[existingIndex] = task
                    } else {
                        todos.add(task)
                    }
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
                todos.find { it.uid == taskId }
            }
            
            EditTaskScreen(
                todoItem = task,
                navController = navController,
                saveChanges = { updatedTask ->
                    val existingIndex = todos.indexOfFirst { it.uid == updatedTask.uid }
                    if (existingIndex >= 0) {
                        todos[existingIndex] = updatedTask
                    } else {
                        todos.add(updatedTask)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TaskListScreen(
    todos: MutableList<TodoItem>,
    onTaskClick: (TodoItem) -> Unit,
    onAddTaskClick: () -> Unit,
    onDeleteTask: (TodoItem) -> Unit,
    onSaveTask: (TodoItem) -> Unit
) {
    Scaffold(
        floatingActionButton = {
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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
            }

            if (todos.isEmpty()) {
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
                        items = todos,
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
                    Spacer(modifier = Modifier.width(8.dp))
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
/*
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
                    deadline = "31-12-2024",
                    isDone = false
                )
            )
        }

        TaskListScreen(
            todos = testTodos,
            onTaskClick = { },
            onAddTaskClick = { },
            onDeleteTask = { },
            onSaveTask = { }
        )
    }
}
*/