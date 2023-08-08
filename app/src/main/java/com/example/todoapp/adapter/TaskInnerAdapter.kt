import android.app.AlertDialog
import android.content.Context
import android.graphics.Paint
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
import com.example.todoapp.model.Task

class TaskInnerAdapter(private val tasks: MutableList<Task>) :
    RecyclerView.Adapter<TaskInnerAdapter.TaskViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(taskPosition: Int)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskDescription: TextView = itemView.findViewById(R.id.taskDescriptionTextView)
        val quantity: TextView = itemView.findViewById(R.id.quantityTextView)
        val specificModel: TextView = itemView.findViewById(R.id.specificModelTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = tasks[position]
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
            holder.quantity.paintFlags = holder.quantity.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.specificModel.paintFlags =
                holder.specificModel.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }

        holder.itemView.findViewById<ImageView>(R.id.editTaskButton).apply {
            tag = position
            setOnClickListener {
                showEditDialog(currentTask, position, it.context)
            }
        }

        holder.itemView.findViewById<ImageView>(R.id.deleteTaskButton).apply {
            tag = position
            setOnClickListener {
                deleteTaskClick(currentTask, position, holder.itemView.context)
            }
        }


    }

    override fun getItemCount() = tasks.size

    private fun showEditDialog(task: Task, position: Int, context: Context) {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_edit_task, null)

        val descriptionEditText = dialogView.findViewById<EditText>(R.id.editTaskDescriptionEditText)
        val quantityEditText = dialogView.findViewById<EditText>(R.id.editTaskQuantityEditText)
        val specificModelEditText = dialogView.findViewById<EditText>(R.id.editTaskSpecificModelEditText)

        descriptionEditText.setText(task.description)
        quantityEditText.setText(task.quantity)
        specificModelEditText.setText(task.specificModel)

        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setTitle("Editar Task")
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
                    task.completed // Mantenha o status de concluído
                )
                tasks[position] = updatedTask

                // Notifique o adapter para atualizar a interface
                notifyItemChanged(position)
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
            // Remova a tarefa da lista
            tasks.removeAt(taskPosition)
            // Notifique o adapter sobre a remoção
            notifyItemRemoved(taskPosition)
            // Notifique o adapter sobre a alteração de posição de itens subsequentes
            notifyItemRangeChanged(taskPosition, tasks.size - taskPosition)
        }
        alertDialogBuilder.setNegativeButton("Cancelar", null)

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}
