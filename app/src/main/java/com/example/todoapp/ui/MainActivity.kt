package com.example.todoapp.ui

import CategoryAdapter
import TaskInnerAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.databinding.ActivityMainBinding
import com.example.todoapp.model.Category
import com.example.todoapp.model.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val categoryList = mutableListOf<Category>()
    private val categoryNames = mutableListOf<String>()
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categoryAdapterSpinner: ArrayAdapter<String>
    private val taskAdapter = TaskInnerAdapter(mutableListOf())
    private lateinit var binding: ActivityMainBinding
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

        setupCategoryRecyclerView()
        setupCategorySpinner()

        setupAddCategoryButton()
        setupAddTaskButton()

        // Chamar a função eventListener() para recuperar os dados do Firebase
        eventListener()
    }

    private fun eventListener() {
        // Recupere as categorias
        val categoriesRef = database.child("categories")
        val categoriesListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = mutableListOf<Category>()
                for (categorySnapshot in snapshot.children) {
                    val category = categorySnapshot.getValue(Category::class.java)
                    category?.let { categories.add(it) }
                }

                // Atualize as categorias no adapter
                categoryAdapter.updateCategories(categories)
                Log.d("Firebase", "Dados atualizados com sucesso na Main")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to retrieve categories: ${error.message}")
            }
        }
        categoriesRef.addListenerForSingleValueEvent(categoriesListener)
    }

    private fun setupCategoryRecyclerView() {
        val recyclerView = binding.categoryRecyclerView
        val layoutManager = LinearLayoutManager(this)
        categoryAdapter = CategoryAdapter(categoryList, categoryNames, object : CategoryAdapter.OnCategoryClickListener {
            override fun onCategoryClick(categoryPosition: Int) {
                val clickedCategory = categoryList[categoryPosition]
                val taskRecyclerView = layoutManager.findViewByPosition(categoryPosition)
                    ?.findViewById<RecyclerView>(R.id.taskRecyclerView)

                val taskLayoutManager = LinearLayoutManager(taskRecyclerView?.context)
                val taskInnerAdapter = TaskInnerAdapter(clickedCategory.tasks)
                taskRecyclerView?.layoutManager = taskLayoutManager
                taskRecyclerView?.adapter = taskInnerAdapter
                categoryAdapterSpinner.notifyDataSetChanged()

                taskInnerAdapter.setOnItemClickListener(object : TaskInnerAdapter.OnItemClickListener {
                    override fun onItemClick(taskPosition: Int) {
                        val clickedTask = clickedCategory.tasks[taskPosition]
                        clickedTask.completed = !clickedTask.completed
                        taskInnerAdapter.notifyItemChanged(taskPosition)
                    }
                })
            }
        })
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = categoryAdapter
    }

    private fun setupCategorySpinner() {
        val categorySpinner = binding.categorySpinner
        categoryAdapterSpinner = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categoryNames
        )
        categoryAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapterSpinner
    }

    private fun setupAddCategoryButton() {
        val newCategoryEditText = binding.newCategoryEditText
        val addCategoryButton = binding.addCategoryButton
        addCategoryButton.setOnClickListener {
            val newCategoryName = newCategoryEditText.text.toString()
            if (newCategoryName.isNotBlank()) {
                val newCategory = Category(newCategoryName, mutableListOf())
                categoryList.add(newCategory)
                categoryNames.add(newCategoryName)
                categoryAdapter.notifyDataSetChanged()
                categoryAdapterSpinner.notifyDataSetChanged()
                newCategoryEditText.text.clear()
            }
        }
    }

    private fun setupAddTaskButton() {
        val taskDescriptionEditText = binding.taskDescriptionEditText
        val quantityEditText = binding.quantityEditText
        val specificModelEditText = binding.specificModelEditText
        val categorySpinner = binding.categorySpinner
        val addTaskButton = binding.addTaskButton

        addTaskButton.setOnClickListener {
            val description = taskDescriptionEditText.text.toString()
            val quantity = quantityEditText.text.toString()
            val specificModel = specificModelEditText.text.toString()

            val selectedCategoryName = categorySpinner.selectedItem?.toString()

            if (description.isNotBlank() && quantity.isNotBlank() && !selectedCategoryName.isNullOrBlank()) {
                val task = Task(
                    Random.nextInt().toString(),
                    description,
                    quantity,
                    specificModel,
                    selectedCategoryName
                )
                val selectedCategory = categoryList.find { it.name == selectedCategoryName }
                if (selectedCategory != null) {
                    taskAdapter.addTask(task)
                    taskAdapter.notifyDataSetChanged()

                    selectedCategory.tasks.add(task)
                    categoryAdapter.notifyDataSetChanged()

                    // Adicione a tarefa à categoria no Firebase
                    val tasksRef = database.child("categories")
                        .child(selectedCategoryName).child("tasks")
                    val taskRef = tasksRef.child(task.id)
                    taskRef.setValue(task)
                        .addOnSuccessListener {
                            Log.d("Firebase", "Task added successfully to Firebase")
                            taskDescriptionEditText.text.clear()
                            quantityEditText.text.clear()
                            specificModelEditText.text.clear()
                        }
                        .addOnFailureListener { error ->
                            Log.e("Firebase", "Error adding task to Firebase: ${error.message}")
                            Toast.makeText(
                                this,
                                "Erro ao adicionar a tarefa no Firebase",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                } else {
                    Toast.makeText(
                        this,
                        "Preencha todos os campos corretamente",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun editTaskClick(view: View) {
        val taskPosition = view.tag as Int
        val task = taskAdapter.getTask(taskPosition)

        taskAdapter.showEditDialog(task, taskPosition, this)
        // Não esqueça de chamar a função updateTask após salvar as alterações no diálogo de edição
        taskAdapter.updateTask(task)
    }
}