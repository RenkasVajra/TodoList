package com.example.myapplication
import android.graphics.Color
import java.util.UUID

enum class Importance {
    Неважная, Обычная, Важная
}

data class TodoItem(
    val uid: String = UUID.randomUUID().toString(),
    val text: String,
    val importance: Importance = Importance.Обычная,
    val color: Int? = Color.WHITE,
    val deadline: String?,
    val isDone: Boolean = false
)

