package com.example.todoapp.service

import com.example.todoapp.model.Task
import com.example.todoapp.repository.TaskRepository

class TaskService(private val taskRepository: TaskRepository) {

    fun addTask(task: Task){
        taskRepository.addTask(task)
    }

    fun getTasks(): List<Task>{
        return taskRepository.getTasks()
    }

    fun removeTask(task: Task){
        taskRepository.removeTask(task)
    }

}