import android.app.AlertDialog
import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.databinding.DialogEditTaskBinding
import com.example.todoapp.databinding.ListItemTaskBinding
import com.example.todoapp.model.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class TaskInnerAdapter(private val tasks: MutableList<Task>) :
    RecyclerView.Adapter<TaskInnerAdapter.TaskViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(taskPosition: Int)
    }

    private var itemClickListener: OnItemClickListener? = null
    private val database: DatabaseReference = FirebaseDatabase.getInstance()
        //.reference.child("tasks")
        .reference

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    class TaskViewHolder(val binding: ListItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val taskDescription = binding.taskDescriptionTextView
        val quantity = binding.quantityTextView
        val specificModel = binding.specificModelTextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ListItemTaskBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = tasks[position]
        Log.d("Adapter", "Task at position $position: $currentTask")
        holder.taskDescription.text = currentTask.description
        holder.quantity.text = currentTask.quantity
        holder.specificModel.text = currentTask.specificModel

        if (currentTask.completed) {
            holder.taskDescription.paintFlags =
                holder.taskDescription.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.quantity.paintFlags = holder.quantity.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.specificModel.paintFlags =
                holder.specificModel.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.taskDescription.paintFlags =
                holder.taskDescription.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.quantity.paintFlags =
                holder.quantity.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.specificModel.paintFlags =
                holder.specificModel.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }

        holder.binding.editTaskButton.apply {
            tag = position
            setOnClickListener {
                showEditDialog(currentTask, position, it.context)
            }
        }

        holder.binding.deleteTaskButton.apply {
            tag = position
            setOnClickListener {
                deleteTaskClick(currentTask, position, holder.itemView.context)
            }
        }
    }

    override fun getItemCount() = tasks.size.also {
        Log.d("Adapter", "ItemCount: $it")
    }

    fun showEditDialog(task: Task, position: Int, context: Context) {
        val dialogBinding = DialogEditTaskBinding.inflate(LayoutInflater.from(context))
        val dialoView = dialogBinding.root

        val descriptionEditText = dialogBinding.editTaskDescriptionEditText
        val quantityEditText = dialogBinding.editTaskQuantityEditText
        val specificModelEditText = dialogBinding.editTaskSpecificModelEditText
        descriptionEditText.setText(task.description)
        quantityEditText.setText(task.quantity)
        specificModelEditText.setText(task.specificModel)

        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialoView)
            .setTitle("Editar Tarefa")
            .setPositiveButton("Salvar") { _, _ ->
                val newDescription = descriptionEditText.text.toString()
                val newQuantity = quantityEditText.text.toString()
                val newSpecificModel = specificModelEditText.text.toString()

                // Atualize os detalhes da task na lista
                val updatedTask = Task(
                    task.id,
                    newDescription,
                    newQuantity,
                    newSpecificModel,
                    task.categoryName,
                    task.completed
                )
                tasks[position] = updatedTask

                // Notifique o adapter para atualizar a interface
                notifyItemChanged(position)
                updateTask(updatedTask)
            }
            .setNegativeButton("Cancelar", null)

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun deleteTaskClick(task: Task, taskPosition: Int, context: Context) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Excluir Tarefa")
        alertDialogBuilder.setMessage("Tem certeza de que deseja excluir esta tarefa?")
        alertDialogBuilder.setPositiveButton("Excluir") { _, _ ->
            deleteTask(task.id, taskPosition)
        }
        alertDialogBuilder.setNegativeButton("Cancelar", null)

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun deleteTask(taskId: String, taskPosition: Int) {
        val taskRef = database.child("tasks").child(taskId)

        taskRef.removeValue()
            .addOnSuccessListener {
                tasks.removeAt(taskPosition)
                notifyItemRemoved(taskPosition)
                notifyItemRangeChanged(taskPosition, tasks.size - taskPosition)
                Log.d("Firebase", "Task deleted successfully from Firebase")
            }
            .addOnFailureListener { error ->
                // Handle the error appropriately
                Log.e("Firebase", "Error deleting task from Firebase: ${error.message}")
            }
    }


    fun updateTasks(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }

    fun updateTask(updatedTask: Task) {
        val position = tasks.indexOfFirst { it.id == updatedTask.id }
        if (position != -1) {
            tasks[position] = updatedTask
            notifyItemChanged(position)
            database.child("tasks").child(updatedTask.id).setValue(updatedTask)
            Log.d("Firebase", "Task updated successfully on Firebase")
        }
    }


    fun getTask(position: Int): Task {
        return tasks[position]
    }

    fun addTask(task: Task) {
        val newTaskRef = database.child("tasks").push() // Gera uma chave única
        task.id = newTaskRef.key.toString() // Define o ID gerado como ID da tarefa

        newTaskRef.setValue(task)
            .addOnSuccessListener {
                tasks.add(task)
                notifyDataSetChanged()
                Log.d("Firebase", "Task added successfully to Firebase")
            }
            .addOnFailureListener { error ->
                // Handle the error appropriately
                Log.e("Firebase", "Error adding task to Firebase: ${error.message}")
            }
    }

}
