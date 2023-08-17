package com.example.todoapp.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.todoapp.model.Task

class TaskViewModel {

    val tasksLiveData: MutableLiveData<List<Task>> = MutableLiveData()
}