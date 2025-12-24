package com.example.myapplication

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


interface TodoApiService {

    @GET("todos")
    suspend fun getTodos(): retrofit2.Response<ApiResponse<TodoListResponse>>

    @GET("todos/{id}")
    suspend fun getTodoById(@Path("id") id: String): retrofit2.Response<ApiResponse<TodoItemApi>>

    @POST("todos")
    suspend fun createTodo(@Body todo: TodoItemApi): retrofit2.Response<ApiResponse<TodoItemApi>>

    @PUT("todos/{id}")
    suspend fun updateTodo(@Path("id") id: String, @Body todo: TodoItemApi): retrofit2.Response<ApiResponse<TodoItemApi>>

    @DELETE("todos/{id}")
    suspend fun deleteTodo(@Path("id") id: String): retrofit2.Response<ApiResponse<Unit>>

    companion object {
        private const val BASE_URL = ApiConfig.BASE_URL
        private const val BEARER_TOKEN = ApiConfig.BEARER_TOKEN

        fun create(): TodoApiService {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val authInterceptor = Interceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $BEARER_TOKEN")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(logger)
                .connectTimeout(ApiConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(ApiConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(ApiConfig.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TodoApiService::class.java)
        }
    }
}
