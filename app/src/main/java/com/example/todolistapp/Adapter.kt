package com.example.todolistapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Adapter(private val context: Context) :
    ListAdapter<Data, Adapter.ExampleViewHolder>(DataDiffCallback()) {

    // delete callback
    var onDeleteClick: ((Data) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ExampleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val item = getItem(position)
        holder.task.text = item.taskName
        holder.description.text = item.taskDescription
        holder.deadline.text = formatDate(item.deadline)

        holder.completed.setOnClickListener {
            val currentColor = holder.cardView.cardBackgroundColor.defaultColor
            if (currentColor == ContextCompat.getColor(context, R.color.green)) {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
            } else {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green))
            }
        }

        holder.delete.setOnClickListener {
            onDeleteClick?.invoke(item)
        }
    }

    fun formatDate(deadline: Long?): String {
        if (deadline == null) return "No deadline"
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(deadline))
    }


    inner class ExampleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val task: TextView = view.findViewById(R.id.task)
        val description: TextView = view.findViewById(R.id.description)
        val deadline : TextView = view.findViewById(R.id.deadline)
        val completed: ImageView = view.findViewById(R.id.taskCompleted)
        val delete: ImageView = view.findViewById(R.id.deleteIcon)
        val cardView: CardView = view.findViewById(R.id.cardView)
    }

    class DataDiffCallback : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            // compare primary key (id in your entity)
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            // compare entire object for equality
            return oldItem == newItem
        }
    }
}
