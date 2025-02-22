package com.example.todo.fragments

import android.app.Application
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.todo.R
import com.example.todo.data.models.Priority
import com.example.todo.data.models.ToDoData

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    val emptyDatabase : MutableLiveData<Boolean> = MutableLiveData(false)

    fun checkIfDatabaseEmpty(toDoData: List<ToDoData>){
        emptyDatabase.value = toDoData.isEmpty()
    }

    val listener: AdapterView.OnItemSelectedListener = object :
        AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(p0: AdapterView<*>?) {}
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            when(position){
                0 -> { (parent?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(application, R.color.red)) }
                1 -> { (parent?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(application, R.color.blue)) }
                2 -> { (parent?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(application, R.color.orange)) }
            }
        }
    }

    fun inputCheck(title: String, description: String): Boolean {

        return if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description))
            false
        else !(TextUtils.isEmpty(title) || TextUtils.isEmpty(title))

    }

    fun parsePriority(priority: String): Priority {
        return when (priority) {
            "High Priority" -> Priority.High
            "Medium Priority" -> Priority.Medium
            "Low Priority" -> Priority.Low
            else -> Priority.Low
        }
    }

    fun parsePriorityToInt(priority: Priority):Int{
        return when(priority){
            Priority.High -> 0
            Priority.Medium -> 1
            Priority.Low -> 2
        }
    }

}