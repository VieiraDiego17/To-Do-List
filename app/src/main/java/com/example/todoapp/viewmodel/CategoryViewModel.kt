package com.example.todoapp.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.todoapp.model.Category

class CategoryViewModel {

    val categoriesLiveData: MutableLiveData<List<Category>> = MutableLiveData()
}