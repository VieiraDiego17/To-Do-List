package com.example.todoapp.model

data class Category(
    val name: String,
    val tasks: MutableList<Task>
)

