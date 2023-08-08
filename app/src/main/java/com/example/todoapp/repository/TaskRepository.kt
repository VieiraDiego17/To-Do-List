package com.example.todoapp.repository

import com.example.todoapp.model.Task

class TaskRepository {
    private val tasks = mutableListOf<Task>()

    fun addTask(task: Task){
        tasks.add(task)
    }

    fun getTasks(): List<Task>{
        return tasks.toList()
    }

    fun removeTask(task: Task){
        tasks.remove(task)
    }

}