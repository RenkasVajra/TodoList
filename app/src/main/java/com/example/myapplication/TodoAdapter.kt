package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class TodoAdapter(private val items: List<TodoItem>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.todoText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item_layout, parent, false)
        return TodoViewHolder(view)
    }
    //
    override fun onBindViewHolder(
        holder: TodoViewHolder,
        position: Int
    ) {
        TODO("Not yet implemented")
    }

    //
    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

}
