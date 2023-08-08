import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.model.Category

class CategoryAdapter(private val categoryList: List<Category>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.categoryNameTextView)
        val taskRecyclerView: RecyclerView = itemView.findViewById(R.id.taskRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_category, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val currentCategory = categoryList[position]
        holder.categoryName.text = currentCategory.name

        val layoutManager = LinearLayoutManager(holder.taskRecyclerView.context)
        val taskInnerAdapter = TaskInnerAdapter(currentCategory.tasks)
        holder.taskRecyclerView.layoutManager = layoutManager
        holder.taskRecyclerView.adapter = taskInnerAdapter

        taskInnerAdapter.setOnItemClickListener(object : TaskInnerAdapter.OnItemClickListener {
            override fun onItemClick(taskPosition: Int) {
                val clickedTask = currentCategory.tasks[taskPosition]
                clickedTask.completed = !clickedTask.completed
                taskInnerAdapter.notifyItemChanged(taskPosition)
            }
        })
    }

    override fun getItemCount() = categoryList.size
}
