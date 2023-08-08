package com.example.todoapp.ui

import CategoryAdapter
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.model.Category
import com.example.todoapp.model.Task
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    private val categoryList = mutableListOf<Category>()
    private val categoryNames = mutableListOf<String>()
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categoryAdapterSpinner: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupCategoryRecyclerView()
        setupCategorySpinner()

        setupAddCategoryButton()
        setupAddTaskButton()

    }

    private fun setupCategoryRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.categoryRecyclerView)
        val layoutManager = LinearLayoutManager(this)
        categoryAdapter = CategoryAdapter(categoryList)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = categoryAdapter
    }

    private fun setupCategorySpinner() {
        val categorySpinner: Spinner = findViewById(R.id.categorySpinner)
        categoryAdapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
        categoryAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapterSpinner
    }


    private fun setupAddCategoryButton() {
        val newCategoryEditText: EditText = findViewById(R.id.newCategoryEditText)
        val addCategoryButton: Button = findViewById(R.id.addCategoryButton)
        addCategoryButton.setOnClickListener {
            val newCategoryName = newCategoryEditText.text.toString()
            if (newCategoryName.isNotBlank()) {
                val newCategory = Category(newCategoryName, mutableListOf())
                categoryList.add(newCategory)
                categoryNames.add(newCategoryName) // Adicione o novo nome à lista
                categoryAdapter.notifyDataSetChanged() // Notifique o adaptador do RecyclerView
                categoryAdapterSpinner.notifyDataSetChanged() // Notifique o adaptador do Spinner
                newCategoryEditText.text.clear()
            }
        }
    }

    private fun setupAddTaskButton() {
        val taskDescriptionEditText: EditText = findViewById(R.id.taskDescriptionEditText)
        val quantityEditText: EditText = findViewById(R.id.quantityEditText)
        val specificModelEditText: EditText = findViewById(R.id.specificModelEditText)
        val categorySpinner: Spinner = findViewById(R.id.categorySpinner)
        val addTaskButton: Button = findViewById(R.id.addTaskButton)
        addTaskButton.setOnClickListener {
            val description = taskDescriptionEditText.text.toString()
            val quantity = quantityEditText.text.toString()
            val specificModel = specificModelEditText.text.toString()

            // Verifique se uma categoria foi selecionada antes de prosseguir
            val selectedCategoryName = categorySpinner.selectedItem?.toString()

            if (description.isNotBlank() && quantity.isNotBlank() && !selectedCategoryName.isNullOrBlank()) {
                val task = Task(Random.nextInt(), description, quantity, specificModel, selectedCategoryName)
                val selectedCategory = categoryList.find { it.name == selectedCategoryName }
                selectedCategory?.tasks?.add(task)
                categoryAdapter.notifyDataSetChanged()
                taskDescriptionEditText.text.clear()
                quantityEditText.text.clear()
                specificModelEditText.text.clear()
            } else {
                Toast.makeText(this, "Preencha todos os campos corretamente", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun editTaskClick(view: View) {
        val taskPosition = view.tag as Int // Obtenha a posição da tarefa da tag do botão
        val selectedCategory = categoryList[0] // Altere isso para obter a categoria correta
        val task = selectedCategory.tasks[taskPosition] // Obtenha a tarefa da categoria
        
    }
}


