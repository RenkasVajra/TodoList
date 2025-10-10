package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.UUID


class MainActivity : AppCompatActivity() {

    private lateinit var prioritySpinner: Spinner
    private lateinit var taskInput: EditText
    private lateinit var addButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TodoAdapter
    annotation class TodoAdapter

    private val todoItems = mutableListOf<TodoItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // recyclerView = findViewById(R.id.recyclerView)

        todoItems.add(TodoItem(
            uid = UUID.randomUUID().toString(),
            text = "Пример задачи",
            importance = Importance.Обычная,
            color = null,
            deadline = null,
            isDone = false
        ))

        recyclerView.layoutManager = LinearLayoutManager(this)

        prioritySpinner = findViewById(R.id.recyclerView)
        addButton = findViewById(R.id.buttonAdd)

    }
    // это на страницу создания задачи
    // taskInput = findViewById(R.id.adddescription)


}

