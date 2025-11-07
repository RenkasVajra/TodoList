package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.NavController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ch.qos.logback.classic.LoggerContext
import org.slf4j.LoggerFactory
import java.util.UUID


class MainActivity : ComponentActivity() {
    private val logger = LoggerFactory.getLogger(MainActivity::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    val todos = remember { mutableStateOf(mutableListOf<TodoItem>()) }
    val newTaskTitle = remember { mutableStateOf(TextFieldValue()) }
    val logger = LoggerFactory.getLogger(MainActivity::class.java)

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(todos.value) { _, todo ->
                TodoItem(todo = todo)
            }
        }

        AddNewTaskForm(newTaskTitle.value, onAddTask = { title ->
            todos.value.add(TodoItem(UUID.randomUUID().toString(), title, deadline = ""))
            newTaskTitle.value = TextFieldValue("")
            logger.info("Добавлена новая задача: $title")
        })
    }
}

@Composable
fun TodoItem(todo: TodoItem) {
    Card(modifier = Modifier.padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = todo.isDone, onCheckedChange = {})
            Text(text = "${todo.text}, срок: ${todo.deadline}")
        }
    }
}

@Composable
fun AddNewTaskForm(title: TextFieldValue, onAddTask: (String) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {

        Button(
            onClick = { onAddTask(title.text.trim()) },
            enabled = title.text.isNotBlank(),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Добавить задачу")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewMainContent() {
    MyApplicationTheme {
        MainContent()
    }
}

