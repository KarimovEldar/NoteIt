package com.example.todo.fragments.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.data.models.Priority
import com.example.todo.data.models.ToDoData
import com.example.todo.databinding.RowLayoutBinding
import com.example.todo.fragments.list.ListFragmentDirections

class ListAdapter:RecyclerView.Adapter<ListAdapter.ToDoViewHolder>() {

    var dataList = emptyList<ToDoData>()

    class ToDoViewHolder(val binding:RowLayoutBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        return ToDoViewHolder(
            RowLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        val currentItem = dataList[position]

        holder.binding.titleTextView.text = currentItem.title
        holder.binding.descriptionTextView.text = currentItem.description
        holder.binding.rowBackground.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToUpdateFragment(currentItem)
            holder.itemView.findNavController().navigate(action)
        }

        when(currentItem.priority){

            Priority.High ->
                holder.binding.priorityIndicator.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.red
                )
            )
            Priority.Medium ->
                holder.binding.priorityIndicator.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.blue
                    )
                )
            Priority.Low ->
                holder.binding.priorityIndicator.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.orange
                    )
                )

        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(newList : List<ToDoData>){
        val toDoDiffUtil = ToDoDiffUtil(newList,dataList)
        val toDoDiffResult = DiffUtil.calculateDiff(toDoDiffUtil)
        this.dataList = newList
        toDoDiffResult.dispatchUpdatesTo(this)
    }

}