package com.example.myapplication

import com.google.gson.annotations.SerializedName


data class TodoItemApi(
    @SerializedName("id")
    val id: String,

    @SerializedName("text")
    val text: String,

    @SerializedName("importance")
    val importance: String,

    @SerializedName("deadline")
    val deadline: String? = null,

    @SerializedName("done")
    val done: Boolean = false,

    @SerializedName("color")
    val color: Int? = null,

    @SerializedName("created_at")
    val createdAt: Long? = null,

    @SerializedName("changed_at")
    val changedAt: Long? = null
)


fun TodoItem.toApiModel(): TodoItemApi {
    return TodoItemApi(
        id = uid,
        text = text,
        importance = importance.name,
        deadline = deadline,
        done = isDone,
        color = color,
        changedAt = System.currentTimeMillis()
    )
}

fun TodoItemApi.toInternalModel(): TodoItem {
    return TodoItem(
        uid = id,
        text = text,
        importance = try {
            Importance.valueOf(importance)
        } catch (e: Exception) {
            Importance.Обычная
        },
        deadline = deadline,
        isDone = done,
        color = color
    )
}


data class ApiResponse<T>(
    @SerializedName("status")
    val status: String,

    @SerializedName("data")
    val data: T? = null,

    @SerializedName("message")
    val message: String? = null
)

data class TodoListResponse(
    @SerializedName("todos")
    val todos: List<TodoItemApi>
)
