package com.example.myapplication.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface TodoDao {


    @Query("SELECT * FROM todos ORDER BY createdAt DESC")
    fun getAllTodosFlow(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos ORDER BY createdAt DESC")
    suspend fun getAllTodos(): List<TodoEntity>

    @Query("SELECT * FROM todos WHERE uid = :uid")
    fun getTodoByIdFlow(uid: String): Flow<TodoEntity?>

    @Query("SELECT * FROM todos WHERE uid = :uid")
    suspend fun getTodoById(uid: String): TodoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodos(todos: List<TodoEntity>)

    @Update
    suspend fun updateTodo(todo: TodoEntity)

    @Query("UPDATE todos SET isDone = :isDone, updatedAt = :updatedAt WHERE uid = :uid")
    suspend fun updateTodoDoneStatus(uid: String, isDone: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM todos WHERE uid = :uid")
    suspend fun deleteTodoById(uid: String): Int

    @Query("DELETE FROM todos")
    suspend fun deleteAllTodos()

    @Query("SELECT COUNT(*) FROM todos")
    suspend fun getTodosCount(): Long

    @Query("SELECT * FROM todos WHERE isDone = 0 ORDER BY createdAt DESC")
    fun getUncompletedTodosFlow(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE isDone = 1 ORDER BY createdAt DESC")
    fun getCompletedTodosFlow(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE importance = :importance ORDER BY createdAt DESC")
    fun getTodosByImportanceFlow(importance: String): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE deadline IS NOT NULL AND deadline != '' ORDER BY deadline ASC")
    fun getTodosWithDeadlineFlow(): Flow<List<TodoEntity>>

    @Query("DELETE FROM todos WHERE isDone = 1 AND updatedAt < :timestamp")
    suspend fun deleteOldCompletedTodos(timestamp: Long): Int
}
