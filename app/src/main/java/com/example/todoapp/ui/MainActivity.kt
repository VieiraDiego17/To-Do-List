package com.example.todoapp.ui

import CategoryAdapter
import TaskInnerAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.databinding.ActivityMainBinding
import com.example.todoapp.model.Category
import com.example.todoapp.model.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
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
    private lateinit var tasks: MutableList<Task>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        tasks = mutableListOf()

        setupCategoryRecyclerView()
        setupCategorySpinner()

        setupAddCategoryButton()
        setupAddTaskButton()

        // Chamar a função eventListener() para recuperar os dados do Firebase
        eventListener()

    }

    override fun onStart() {
        super.onStart()
        eventListener() // Atualiza os dados ao iniciar a atividade
    }

    override fun onResume() {
        super.onResume()
        eventListener() // Atualiza os dados ao retomar a atividade
    }

    private fun eventListener() {
        val database = FirebaseDatabase.getInstance().reference
        val tasksRef = database.child("tasks")

        val tasksListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                val newTasks = mutableListOf<Task>()
                for (taskSnapshot in snapshot.children) {
                    val task = taskSnapshot.getValue(Task::class.java)
                    task?.let { newTasks.add(it) }
                }

                // Limpa a lista atual e adiciona os novos dados
                tasks.clear()
                tasks.addAll(newTasks)

                // Log para depuração
                Log.d("Firebase", "Retrieved tasks: ${tasks.joinToString(", ")}")

                // Atualiza os dados no adaptador
                taskAdapter.updateTasks(tasks)
                taskAdapter.notifyDataSetChanged() // Notificar a atualização para atualizar a UI
            }

            override fun onCancelled(error: DatabaseError) {
                // Tratar erros de leitura do Firebase
                Log.e("Firebase", "Failed to retrieve tasks: ${error.message}")
            }
        }

        tasksRef.addValueEventListener(tasksListener)
    }



    private fun setupCategoryRecyclerView() {
        val recyclerView = binding.categoryRecyclerView
        val layoutManager = LinearLayoutManager(this)
        categoryAdapter = CategoryAdapter(categoryList)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = categoryAdapter
    }

    private fun setupCategorySpinner() {
        val categorySpinner = binding.categorySpinner
        categoryAdapterSpinner =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
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

                taskAdapter.addTask(task)
                taskAdapter.notifyDataSetChanged()

                selectedCategory?.tasks?.add(task)
                categoryAdapter.notifyDataSetChanged()

                taskDescriptionEditText.text.clear()
                quantityEditText.text.clear()
                specificModelEditText.text.clear()
            } else {
                Toast.makeText(this, "Preencha todos os campos corretamente", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun editTaskClick(view: View) {
        val taskPosition = view.tag as Int // Obtenha a posição da tarefa da tag do botão
        val task = taskAdapter.getTask(taskPosition) // Obtenha a tarefa do adapter

        // Abra o diálogo de edição de tarefa
        taskAdapter.showEditDialog(
            task,
            taskPosition,
            this
        ) // Substitua this pelo contexto apropriado

        // Ao salvar as alterações no diálogo de edição, você pode chamar a função updateTask
        //taskAdapter.updateTask(task)

        // Caso você esteja usando o adaptador de categorias para exibir as tarefas na lista,
        // você também pode chamar a função updateTasks para atualizar a lista de tarefas da categoria
        //selectedCategory.tasks = taskAdapter.tasks
        categoryAdapter.notifyDataSetChanged()
    }
}


