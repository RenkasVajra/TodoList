package com.example.myapplication.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myapplication.Importance
import com.example.myapplication.TodoItem

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey
    val uid: String,
    val text: String,
    val importance: String, // Сохраняем как строку для совместимости
    val color: Int? = null,
    val deadline: String? = null,
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

fun TodoEntity.toTodoItem(): TodoItem {
    return TodoItem(
        uid = uid,
        text = text,
        importance = try {
            Importance.valueOf(importance)
        } catch (e: Exception) {
            Importance.Обычная // fallback для старых данных
        },
        color = color,
        deadline = deadline,
        isDone = isDone
    )
}

fun TodoItem.toTodoEntity(): TodoEntity {
    return TodoEntity(
        uid = uid,
        text = text,
        importance = importance.name,
        color = color,
        deadline = deadline,
        isDone = isDone,
        updatedAt = System.currentTimeMillis()
    )
}