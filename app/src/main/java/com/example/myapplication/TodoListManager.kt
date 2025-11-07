package com.example.myapplication

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.dataconnect.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.schedule


class TodoListManager {

    private val todoItems = mutableListOf<TodoItem>()

    fun addItem(item: TodoItem) {
        todoItems.add(item)
    }

    fun getItems(): List<TodoItem> {
        return todoItems.toList()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkForExpiredItems() {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        todoItems.removeIf { item ->
            item.deadline?.let { deadlineStr ->
                val deadline = LocalDateTime.parse(deadlineStr, formatter)
                deadline.isBefore(now)
            } ?: false
        }

<<<<<<< HEAD
        @RequiresApi(Build.VERSION_CODES.O)
        fun startAutoCleanup() {
            Timer().schedule(delay = 0, period = 60_000) {
                checkForExpiredItems()
            }
        }
    }
}
=======
    @RequiresApi(Build.VERSION_CODES.O)
    fun startAutoCleanup() {
        Timer().schedule(delay = 0, period = 60_000) {
            checkForExpiredItems()
        }
    }

}
>>>>>>> e641694f437f91ab5b478bc8f5f84eee7316c40b
