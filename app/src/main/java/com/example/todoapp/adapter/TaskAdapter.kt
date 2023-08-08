package com.example.todoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.databinding.ListItemTaskBinding
import com.example.todoapp.model.Task

class TaskAdapter(private val taskList: MutableList<Task>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    inner class TaskViewHolder(private val binding: ListItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener?.onItemClick(position)
                }
            }
        }

        fun bind(task: Task) {
            binding.taskDescriptionTextView.text = task.description
            binding.quantityTextView.text = task.quantity
            binding.specificModelTextView.text = task.specificModel
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding =
            ListItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun getItemCount() = taskList.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = taskList[position]
        holder.bind(currentTask)
    }

    fun addTask(task: Task) {
        taskList.add(task)
        notifyItemInserted(taskList.size - 1)
    }
}
