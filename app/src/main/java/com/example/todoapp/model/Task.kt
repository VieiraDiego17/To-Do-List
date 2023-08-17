package com.example.todoapp.model

data class Task(
    var id: String = "",
    var description: String = "",
    var quantity: String = "",
    var specificModel: String = "",
    var categoryName: String = "",
    var completed: Boolean = false
)
