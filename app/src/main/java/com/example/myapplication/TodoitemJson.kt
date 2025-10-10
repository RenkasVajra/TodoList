package com.example.myapplication
import org.json.JSONObject
import android.graphics.Color


fun TodoItem.toJson(): JSONObject {
    val json = JSONObject()
    json.put("uid", uid)
    json.put("text", text)
    if (importance != Importance.Обычная) {
        json.put("importance", importance.name)
    }
    color?.let {
        if (it != Color.WHITE) {
            json.put("color", it)
        }
    }
    deadline?.let { json.put("deadline", it) }
    json.put("isDone", isDone)
    return json
}

fun JSONObject.toTodoItem(): TodoItem? {
    return try {
        val uid = getString("uid")
        val text = getString("text")
        val importance = optString("importance")?.let { Importance.valueOf(it) } ?: Importance.Обычная
        val color = optInt("color", Color.WHITE).takeIf { it != Color.WHITE }
        val deadline = optString("deadline", null).takeIf { it != "null" }
        val isDone = getBoolean("isDone")
        TodoItem(uid, text, importance, color, deadline, isDone)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
