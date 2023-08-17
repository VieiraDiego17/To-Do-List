// CategoryAdapter.kt
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.model.Category
import com.example.todoapp.model.Task
import com.google.firebase.database.FirebaseDatabase

class CategoryAdapter(
    private val categoryList: MutableList<Category>,
    private val categoryNames: MutableList<String>,
    private val categoryClickListener: OnCategoryClickListener?
) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    interface OnCategoryClickListener {
        fun onCategoryClick(categoryPosition: Int)
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.categoryNameTextView)
        val taskRecyclerView: RecyclerView = itemView.findViewById(R.id.taskRecyclerView)
        val deleteCategoryButton: ImageView = itemView.findViewById(R.id.deleteCategoryButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_category, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val currentCategory = categoryList[position]
        holder.categoryName.text = currentCategory.name

        val layoutManager = LinearLayoutManager(holder.taskRecyclerView.context)
        val taskInnerAdapter = TaskInnerAdapter(currentCategory.tasks as MutableList<Task>)
        holder.taskRecyclerView.layoutManager = layoutManager
        holder.taskRecyclerView.adapter = taskInnerAdapter

        taskInnerAdapter.setOnItemClickListener(object : TaskInnerAdapter.OnItemClickListener {
            override fun onItemClick(taskPosition: Int) {
                val clickedTask = currentCategory.tasks[taskPosition]
                clickedTask.completed = !clickedTask.completed
                taskInnerAdapter.notifyItemChanged(taskPosition)
            }
        })

        holder.deleteCategoryButton.setOnClickListener {
            onDeleteCategoryClick(currentCategory, holder.itemView.context)
        }
    }

    private fun onDeleteCategoryClick(category: Category, context: Context) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Excluir Categoria")
        alertDialogBuilder.setMessage("Tem certeza de que deseja excluir esta categoria e todas as suas tarefas?")
        alertDialogBuilder.setPositiveButton("Excluir") { _, _ ->
            deleteCategory(category)
            val removedPosition = categoryList.indexOf(category)
            if (removedPosition != -1) {
                categoryList.removeAt(removedPosition)
                notifyItemRemoved(removedPosition)
            }
            // Remover o nome da categoria da lista categoryNames
            categoryNames.remove(category.name)
            notifyDataSetChanged()

        }
        alertDialogBuilder.setNegativeButton("Cancelar", null)

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun deleteCategory(category: Category) {
        val database = FirebaseDatabase.getInstance().reference
        val categoryRef = database.child("categories").child(category.name)

        categoryRef.removeValue()
            .addOnSuccessListener {
                Log.d("Firebase", "Category deleted successfully from Firebase")
            }
            .addOnFailureListener { error ->
                Log.e("Firebase", "Error deleting category from Firebase: ${error.message}")
            }
    }

    fun updateCategories(newCategories: List<Category>) {
        categoryList.clear()
        categoryList.addAll(newCategories)
        notifyDataSetChanged()
    }

    override fun getItemCount() = categoryList.size
}
