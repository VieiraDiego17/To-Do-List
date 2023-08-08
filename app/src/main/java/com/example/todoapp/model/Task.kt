package com.example.todoapp.model

data class Task(
    val id: Int,
    val description: String,
    val quantity: String,
    val specificModel: String,
    val categoryName: String,
    var completed: Boolean = false
)
